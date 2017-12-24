package usecases.rmrs

import com.firstlinesoftware.base.client.utils.StringUtils
import com.firstlinesoftware.base.server.exceptions.ServerException
import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.base.server.services.TenantResourceService
import com.firstlinesoftware.base.shared.dto.AttachedFile
import com.firstlinesoftware.ecm.server.providers.impl.CompositeDocumentProvider
import com.firstlinesoftware.ecm.server.services.DocumentService
import com.firstlinesoftware.ecm.shared.dto.RelatedDocument
import com.firstlinesoftware.orgstruct.server.services.OrgstructService
import com.firstlinesoftware.orgstruct.shared.dto.Position
import com.firstlinesoftware.orgstruct.shared.dto.Person
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter
import com.firstlinesoftware.rmrs.shared.dto.Requirement
import com.jacob.activeX.ActiveXComponent
import com.jacob.com.Variant
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.math.NumberUtils
import org.apache.log4j.Logger
import org.apache.poi.xwpf.usermodel.IBodyElement
import org.apache.poi.xwpf.usermodel.BodyElementType
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFPicture
import org.apache.poi.xwpf.usermodel.XWPFRun
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.apache.poi.xwpf.usermodel.XWPFTableRow
import org.apache.poi.xwpf.usermodel.BreakType
import org.apache.commons.lang3.SystemUtils
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import usecases.base.BaseUC

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.POST
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import com.firstlinesoftware.base.server.utils.PoiWordUtils
import com.firstlinesoftware.base.server.utils.Messages
import org.apache.xmlbeans.*
import org.openxmlformats.schemas.drawingml.x2006.main.*
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline

import java.text.DateFormat
import java.text.SimpleDateFormat

@SuppressWarnings("GroovyUnusedDeclaration")
@Component
class PrintCircularLetterUC extends BaseUC {

    private static final String RUSSIAN = 'ru'
    private static final String ENGLISH = 'en'
    private static final String STR_HYPHEN = '-'
    private static final String PDF_MIME_TYPE = 'application/pdf'
    private static final String DOCX_MIME_TYPE = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
    private DateFormat dateFormat = new SimpleDateFormat('dd.MM.yyyy')

    private File tempDir

    @Autowired
    protected Messages messages
    @Autowired
    protected RepositoryService repositoryService
    @Autowired
    protected TenantResourceService tenantResourceService
    @Autowired
    protected OrgstructService orgstructService
    @Autowired
    private DocumentService documentService

    @POST
    Object run(final CircularLetter letter, @QueryParam('reportName') final String reportName, @QueryParam('lang') final String lang, @Context final HttpServletRequest request) {

        tempDir = new File(System.getProperty("java.io.tmpdir"), "aurora")

        try {
            if(lang.equals(ENGLISH)) {
                messages.setLocale(Locale.ENGLISH)
            }

            final File template = getTemplate(reportName + '_' + lang + '.docx')
            final XWPFDocument document = new XWPFDocument(new FileInputStream(template))

            createReport(letter, document, lang)

            File result = File.createTempFile('report_', '.docx', repositoryService.getTempDir())
            document.write(new FileOutputStream(result))

            return result.getName()
        } catch (IOException e) {
            throw new RuntimeException(e)
        }
    }

    protected void createReport(CircularLetter letter, XWPFDocument document, String lang) {

        Map<String, String> vars = new HashMap<String, String>()

        vars.put('number', letter.number != null ? letter.number : letter.businessCaseNumber ?: STR_HYPHEN)
        vars.put('signingDate', letter.getCreated() != null ? dateFormat.format(letter.getCreated()) : STR_HYPHEN)
        vars.put('links', getLinksString(letter, lang))
        vars.put('authorPhone', getPhoneNumber(letter.author))
        vars.put('requirements', STR_HYPHEN)

        if(lang.equals(RUSSIAN)) {
            vars.put('signingRank', letter.approvePosition != null ? letter.approvePosition.getName() : STR_HYPHEN)
            vars.put('signingName', letter.approvePosition != null ? getShortenedName(letter.approvePosition, true) : STR_HYPHEN)
            vars.put('authorName', letter.getAuthor() != null ? getShortenedName(letter.getAuthor(), false) : STR_HYPHEN)
            vars.put('authorDC', letter.getAuthor() != null ? (letter.getAuthor().getDepartment().getName() ?: STR_HYPHEN) : STR_HYPHEN)

            vars.put('referred', letter.getName() ?: STR_HYPHEN)
            vars.put('observable', letter.observable ?: STR_HYPHEN)
            vars.put('commissioning', letter.commissioning ?: STR_HYPHEN)
            vars.put('validTo', letter.validTo ?: STR_HYPHEN)
            vars.put('validExtendedUntil', letter.validExtendedUntil ?: STR_HYPHEN)
        }
        if(lang.equals(ENGLISH)) {
            vars.put('signingRank', StringUtils.STR_EMPTY)
            vars.put('signingName', StringUtils.STR_EMPTY)
            vars.put('authorName', StringUtils.STR_EMPTY)
            vars.put('authorDC', StringUtils.STR_EMPTY)

            vars.put('referred', letter.referred_en ?: STR_HYPHEN)
            vars.put('observable', letter.observable_en ?: STR_HYPHEN)
            vars.put('commissioning', letter.commissioning_en ?: STR_HYPHEN)
            vars.put('validTo', letter.validTo_en ?: STR_HYPHEN)
            vars.put('validExtendedUntil', letter.validExtendedUntil_en ?: STR_HYPHEN)
        }

        PoiWordUtils.resolveVarInDoc(vars, document)

        fillPlaceHolder(document, 'documentsToChange', getRequirementsTopString(letter, lang))

        if(lang.equals(RUSSIAN)) {
            fillPlaceHolder(document, 'content', letter.content ?: STR_HYPHEN)
            fillPlaceHolder(document, 'actions', letter.action ?: STR_HYPHEN)
        } else if(lang.equals(ENGLISH)) {
            fillPlaceHolder(document, 'content', letter.content_en ?: STR_HYPHEN)
            fillPlaceHolder(document, 'actions', letter.action_en ?: STR_HYPHEN)
        }

        if(letter.changedContent.isEmpty() && (letter.approvedRequirements == null || letter.approvedRequirements.size() == 0)){
            PoiWordUtils.setVar(document, 'appendix', STR_HYPHEN)
            removeAppendixPage(document)
        } else {
            PoiWordUtils.setVar(document, 'appendix', messages.getMessage('circularAppendix'))

            fillPlaceHolder(document, 'modifyDetails', letter.changedContent ?: STR_HYPHEN)

            if((letter.approvedRequirements != null && letter.approvedRequirements.size() != 0)) {
                addRequirementTable(document, letter, lang)
            }
        }

        // doesn't work, return value of template file
//        int numPages = document.getProperties().getExtendedProperties().underlyingProperties.pages
//        fillPlaceHolder(document, 'pagesCount', String.valueOf(numPages))
    }


    private File getTemplate(String name) {
        return tenantResourceService.lookupFileInFolder(getClass().getResource('/reports'), name, false)
    }

    private String getPhoneNumber(Position position) {
        if (position != null && position.person != null) {
            Person person = orgstructService.getPerson(position.person.id)
            if (person != null && person.mobile != null && !person.mobile.isEmpty()) {
                return person.mobile
            }
        }
        return STR_HYPHEN
    }

    private String getShortenedName(Position position, boolean reverse) {

        if(position == null || position.person == null || position.person.id == null) {
            return null
        }

        Person person = orgstructService.getPerson(position.person.id)

        String lastName = person.getLastName()
        String firstName = person.getFirstName()
        String middleName = person.getMiddleName()

        StringBuilder out = new StringBuilder()

        if(reverse) {
            if (firstName != null && !firstName.isEmpty()) {
                out.append(firstName.charAt(0))
                out.append(StringUtils.STR_DOT)
            }
            if (middleName != null && !middleName.isEmpty()) {
                out.append(middleName.charAt(0))
                out.append(StringUtils.STR_DOT)
            }
            if (lastName != null && !lastName.isEmpty()) {
                out.append(StringUtils.STR_SPACE )
                out.append(lastName)
            }
        } else {
            if (lastName != null && !lastName.isEmpty()) {
                out.append(lastName)
                out.append(StringUtils.STR_SPACE )
            }
            if (firstName != null && !firstName.isEmpty()) {
                out.append(firstName.charAt(0))
                out.append(StringUtils.STR_DOT)
            }
            if (middleName != null && !middleName.isEmpty()) {
                out.append(middleName.charAt(0))
                out.append(StringUtils.STR_DOT)
            }
        }

        return out.size() > 0 ? out.toString() : STR_HYPHEN
    }

    private String getLinksString(CircularLetter letter, String lang) {

        StringBuilder out = new StringBuilder()

        if (letter.getRelatedDocuments() != null && !letter.getRelatedDocuments().isEmpty()) {
            boolean first = true
            for(RelatedDocument relatedDoc: letter.getRelatedDocuments()){
                if(first) first = false
                else out.append(', ')

                out.append(relatedDoc.getDocument().getKind())
                out.append(' №')
                out.append(relatedDoc.getDocument().getDocumentNumber())
                if(lang.equals(RUSSIAN)) {
                    out.append(' от ')
                } else if(lang.equals(ENGLISH)) {
                    out.append(' dated ')
                }
                out.append(dateFormat.format(relatedDoc.getDocument().getCreated()))
            }
        }

        return out.size() > 0 ? out.toString() : STR_HYPHEN
    }

    private String getRequirementsTopString(CircularLetter letter, String lang) {
        StringBuilder result = null
        if(letter.approvedRequirements != null && letter.approvedRequirements.size() > 0) {
            final TreeSet<Requirement> set = new TreeSet<>(new Comparator<Requirement>() {
                @Override
                int compare(Requirement requirement1, Requirement requirement2) {
                    if(lang == RUSSIAN) {
                        if(requirement1.name != null) {
                            return requirement1.name <=> requirement2.name
                        } else return 1
                    } else {
                        if(requirement1.englishText != null) {
                            return requirement1.englishText <=> requirement2.englishText
                        } else return 1
                    }
                }
            })
            for (Requirement requirement : letter.approvedRequirements) {
                set.add(getTopRequirement(requirement))
            }

            result = new StringBuilder()
            for (Requirement requirement : set) {
                String name
                if (lang == RUSSIAN) {
                    name = requirement.name
                } else {
                    name = requirement.englishText
                }
                if (name != null && !name.isEmpty()) {
                    result.append(name.substring(0, 1).toUpperCase()).append(name.substring(1).toLowerCase())
                    if (requirement.number != null && !requirement.number.isEmpty()) {
                        result.append(', ')
                                .append(messages.getMessage('circular.letter.requirement.document.number'))
                                .append(' ').append(requirement.number)
                    }
                    result.append(System.lineSeparator())
                }
            }
            if (result.length() > 0) result.delete(result.length() - 1, result.length())
        }
        return result == null || result.length() == 0 ? STR_HYPHEN : result
    }

    private Requirement getTopRequirement(Requirement requirement) {

        Requirement current = (Requirement)documentService.get(requirement.id)
        if(current.parent == null) {
            return current
        } else {
            return getTopRequirement(current.parent)
        }
    }

    private void fillPlaceHolder(XWPFDocument document, String placeHolder, String value) {

        XWPFTableCell cell = getCellByPlaceHolder(document, placeHolder)
        if(cell == null) {
            return
        }

        XWPFParagraph p
        if (cell.getParagraphs() != null && !cell.getParagraphs().isEmpty()) {
            p = cell.getParagraphs().get(0)
        } else {
            p = cell.addParagraph()
        }

        XWPFRun run = p.getRuns().get(0)
        if (run != null) {
            String[] valuePartMass = value.replaceAll('\n', System.lineSeparator()).split(SystemUtils.LINE_SEPARATOR)
            for (int i = 0; i < valuePartMass.length; i++) {
                String part = valuePartMass[i]
                if(part.isEmpty()) {
                    run.setText(value, 0)
                }
                else {
                    run.setText(part, 0)
                    if(i != valuePartMass.length - 1) {
                        run.addBreak(BreakType.TEXT_WRAPPING)
                    }
                    run = p.createRun()
                }
            }
        }
    }

    private XWPFTableCell getCellByPlaceHolder(XWPFDocument document, String placeHolder) {

        for(XWPFTable table: document.tables) {
            for(XWPFTableRow row: table.rows) {
                for(XWPFTableCell cell: row.getTableCells()) {
                    if(cell.getText() != null && cell.getText().contains(placeHolder)) {
                        return cell
                    }
                }
            }
        }
        return null
    }

    private void removeAppendixPage(XWPFDocument document) {

        String textToSearch = messages.getMessage('circularAppendixPageMark')

        Integer tableToRemoveIndex = null
        for(IBodyElement element: document.bodyElements) {
            if(element.elementType.equals(BodyElementType.TABLE)) {
                for(XWPFTableRow row: ((XWPFTable)element).rows) {
                    for(XWPFTableCell cell: row.getTableCells()) {
                        if(cell.getText().contains(textToSearch)) {
                            tableToRemoveIndex = document.bodyElements.indexOf(element)
                            break
                        }
                    }
                }
            }
        }
        if(tableToRemoveIndex != null) {
            document.removeBodyElement(tableToRemoveIndex)
            document.removeBodyElement(tableToRemoveIndex - 1)
        }
    }


    private void addRequirementTable(XWPFDocument document, CircularLetter letter, String lang) {

        Map<Integer, TreeSet<Requirement>> map = new HashMap<>()
        Comparator<Requirement> comparator = new Comparator<Requirement>() {
            @Override
            int compare(Requirement requirement1, Requirement requirement2) {
                int result = 0
                if(requirement1.number != null && requirement2.number != null) {
                    String[] mass1 = requirement1.number.split("\\.")
                    String[] mass2 = requirement2.number.split("\\.")
                    if(mass1.length == mass2.length) {
                        int lastIndex = mass1.length - 1
                        for(int j = lastIndex; j >= 0; j--) {
                            if (!mass1[j].equals(mass2[j]) && NumberUtils.isNumber(mass1[j]) && NumberUtils.isNumber(mass2[j])) {
                                int number1 = Integer.parseInt(mass1[j])
                                int number2 = Integer.parseInt(mass2[j])
                                if (number1 - number2 > 0) result = 1
                                else if (number1 - number2 < 0) result = -1
                                break
                            }
                        }
                    } else {
                        result = requirement1.number <=> requirement2.number
                    }
                }
                if (result == 0) {
                    String name1
                    String name2
                    if (lang == RUSSIAN) {
                        name1 = requirement1.name
                        name2 = requirement2.name
                    } else {
                        name1 = requirement1.englishText
                        name2 = requirement2.englishText
                    }
                    String firstPart1 = name1.split(" ")[0]
                    String firstPart2 = name1.split(" ")[0]
                    if(lang == RUSSIAN && firstPart1.equals("ЧАСТЬ") && firstPart2.equals("ЧАСТЬ") ||
                            lang == ENGLISH && firstPart1.equals("PART") && firstPart2.equals("PART")) {
                        String[] mass1 = name1.split(" ")
                        String[] mass2 = name2.split(" ")
                        if(!mass1[1].equals(mass2[1]) && NumberUtils.isNumber(mass1[1]) && NumberUtils.isNumber(mass2[1])) {
                            int number1 = Integer.parseInt(mass1[1])
                            int number2 = Integer.parseInt(mass2[1])
                            if (number1 - number2 > 0) result = 1
                            else if (number1 - number2 < 0) result = -1
                        }
                    } else if (name1 != null && name2 != null) {
                        result = name1 <=> name2
                    }
                }
                return result
            }
        }

        for (Requirement requirement : letter.approvedRequirements) {
            List<Requirement> hierarchy = getRequirementHierarchy(requirement, new ArrayList<Requirement>())
            for (int i = 0; i < hierarchy.size(); i++) {
                if (map.get(i) == null) {
                    map.put(i, new TreeSet<Requirement>(comparator))
                }
                map.get(i).add(hierarchy.get(i))
            }
        }

        LinkedHashMap<Requirement, Integer> hierarchyToPrint = createHierarchyToPrint(0, null, map, new LinkedHashMap<Requirement, Integer>())

        for(Map.Entry<Requirement, Integer> entry: hierarchyToPrint.entrySet()) {
            printHierarchyRow(entry, document, lang)
        }
    }

    private List<Requirement> getRequirementHierarchy(Requirement requirement, List<Requirement> hierarchy) {
        Requirement parent = requirement.parent == null ? null : (Requirement)documentService.get(requirement.parent.getId())
        if(parent == null) {
            hierarchy.add(requirement)
            Collections.reverse(hierarchy)
            return hierarchy
        } else {
            hierarchy.add(requirement)
            getRequirementHierarchy(parent, hierarchy)
        }
    }

    private LinkedHashMap<Requirement, Integer> createHierarchyToPrint(Integer index, Requirement previous, Map<Integer, TreeSet<Requirement>> map, LinkedHashMap<Requirement, Integer> hierarchy) {

        TreeSet<Requirement> currentSet = map.get(index)

        if(currentSet != null) {
            for (Requirement req : currentSet) {
                Requirement parent = req.parent == null ? null : (Requirement)documentService.get(req.parent.getId())
                if (previous == null || (parent != null && previous.id.equals(parent.id))) {
                    hierarchy.put(req, index + 1)
                    createHierarchyToPrint(index + 1, req, map, hierarchy)
                }
            }
        }

        return hierarchy
    }

    private void printHierarchyRow(Map.Entry<Requirement, Integer> entry, XWPFDocument document, String lang) {

        XWPFParagraph paragraph = document.createParagraph()
        paragraph.setSpacingAfter(0)

        StringBuilder hyphens = new StringBuilder()
        for(int i = 0; i < entry.value; i++) {
            hyphens.append(STR_HYPHEN)
        }
        XWPFRun runHyphens = paragraph.createRun()
        runHyphens.setText(hyphens.toString())

        XWPFRun runSpace = paragraph.createRun()
        runSpace.setText(StringUtils.STR_SPACE)

        Requirement currentRequirement = (Requirement) documentService.get(entry.key.id)
        XWPFDocument attacheFile = null

        int nameFieldSize = 0
        if(lang == RUSSIAN && currentRequirement.name != null) {
            nameFieldSize = currentRequirement.name.length()
        } else if(lang == ENGLISH && currentRequirement.englishText != null) {
            nameFieldSize = currentRequirement.englishText.length()
        }
        if(currentRequirement.isLeaf || !currentRequirement.header || nameFieldSize >= 255) {
            if (lang == RUSSIAN && currentRequirement.russian != null && currentRequirement.russian.mime != null) {
                if(currentRequirement.russian.mime.equals(PDF_MIME_TYPE)) {
                    attacheFile = convertPDFToDOCX(currentRequirement.russian)
                } else if(currentRequirement.russian.mime.equals(DOCX_MIME_TYPE)){
                    attacheFile = new XWPFDocument(getInputStream(currentRequirement.russian))
                }
            } else if (lang == ENGLISH && currentRequirement.english != null && currentRequirement.english.mime != null) {
                if(currentRequirement.english.mime.equals(PDF_MIME_TYPE)) {
                    attacheFile = convertPDFToDOCX(currentRequirement.english)
                } else if(currentRequirement.english.mime.equals(DOCX_MIME_TYPE)){
                    attacheFile = new XWPFDocument(getInputStream(currentRequirement.english))
                }
            }
        }

        if(attacheFile != null) {

            copyContentDOCX(document, attacheFile, paragraph)

        } else {
            XWPFRun run = paragraph.createRun()
            def number = (entry.key.number ? entry.key.number  + ' ': "")
            def text
            if (lang == RUSSIAN) {
                 text = entry.key.name != null ? entry.key.name.replaceAll('\n', System.lineSeparator()) : STR_HYPHEN
            } else {
                 text = (entry.key.englishText != null ? entry.key.englishText.replaceAll('\n', System.lineSeparator()) : STR_HYPHEN)
            }
            if (text.startsWith(number.trim())) {
                run.setText(text)
            } else {
                run.setText(number + text)
            }
        }

        if(currentRequirement.isLeaf || !currentRequirement.header) {
            XWPFParagraph paragraphForDates = document.createParagraph()

            if(entry.key.getRegistrationDate() != null) {
                XWPFRun registrationDateRun = paragraphForDates.createRun()
                registrationDateRun.setText(messages.getMessage('signedDate') + ': ' + dateFormat.format(entry.key.getRegistrationDate()))
                registrationDateRun.addBreak(BreakType.TEXT_WRAPPING)
            }
            if(entry.key.getStartDate() != null) {
                XWPFRun effectiveBeginDateRun = paragraphForDates.createRun()
                effectiveBeginDateRun.setText(messages.getMessage('effectiveBegin') + ': ' + dateFormat.format(entry.key.getStartDate()))
                effectiveBeginDateRun.addBreak(BreakType.TEXT_WRAPPING)
            }
            if(entry.key.getEndDate() != null) {
                XWPFRun effectiveEndDateRun = paragraphForDates.createRun()
                effectiveEndDateRun.setText(messages.getMessage('effectiveEnd') + ': ' + dateFormat.format(entry.key.getEndDate()))
                effectiveEndDateRun.addBreak(BreakType.TEXT_WRAPPING)
            }
        }
    }

    private InputStream getInputStream(AttachedFile attachedFile) {

        InputStream inputStream = null
        if (attachedFile.uploaded) {
            final RepositoryService.Content content = repositoryService.getContent(attachedFile.id)
            if (content != null) {
                inputStream = content.stream
            }
        } else {
            final File content = CompositeDocumentProvider.storeLocalFiles.get() ? new File(attachedFile.id) : new File(repositoryService.getTempDir(), attachedFile.id)
            try {
                inputStream = new FileInputStream(content)
            } catch (FileNotFoundException e) {
                throw new ServerException("File not found: " + content.getAbsolutePath() + System.lineSeparator() + e.printStackTrace())
            }
        }

        return inputStream
    }

    private XWPFDocument convertPDFToDOCX(AttachedFile attachedFile) {

        if (!attachedFile) {
            return null;
        }
        File source = File.createTempFile("source-", "-" + attachedFile.name, tempDir)
        FileOutputStream from
        try {
            def stream = getInputStream(attachedFile)
            if (stream) {
            from = new FileOutputStream(source)
                IOUtils.copyLarge(stream, from)
            }
        } catch (IOException e) {
            Logger.getLogger(getClass()).error( e)
        } finally {
            if(from != null) {
                from.close()
            }
        }

        File target = new File(tempDir.toString() + "\\converted-" + attachedFile.name.replaceAll("pdf", "docx"))

        boolean tSaveOnExit = false

        try {
            ActiveXComponent oWord = new ActiveXComponent("Word.Application")
            ActiveXComponent oDocuments = oWord.getPropertyAsComponent("Documents")
            ActiveXComponent oDocument = oDocuments.invokeGetComponent("Open", new Variant(source.getAbsolutePath()))
            ActiveXComponent oWordBasic = oWord.getPropertyAsComponent("WordBasic")
            oWordBasic.invoke("FileSaveAs", target.getAbsolutePath())
            oDocument.invoke("Close", tSaveOnExit)
            oWord.invoke("Quit", 0)
        } catch (Exception e) {
            Logger.getLogger(getClass()).error( e)
            return new XWPFDocument()
        }

        while (!target.exists()) {
            Thread.sleep(100)
        }

        if(target.exists()) {
            InputStream is = new FileInputStream(target)
            return new XWPFDocument(is)
        } else {
            return new XWPFDocument()
        }
    }

    private void copyContentDOCX(XWPFDocument recipient, XWPFDocument donor, XWPFParagraph firstParagraph) {

        boolean first = true

        int minLeftIndent = Integer.MAX_VALUE
        for(IBodyElement bodyElement: donor.getBodyElements()) {
            if (bodyElement.getElementType().name().equals("PARAGRAPH")) {
                int indentation = ((XWPFParagraph) bodyElement).getIndentationLeft()
                if(indentation < 0 && indentation < minLeftIndent) {
                    minLeftIndent = indentation
                }
            }
        }

        for(IBodyElement bodyElement: donor.getBodyElements()) {
            if(bodyElement.getElementType().name().equals("PARAGRAPH")) {
                if (first) {
                    first = false
                    copyParagraph((XWPFParagraph) bodyElement, firstParagraph, true, minLeftIndent)
                } else {
                    XWPFParagraph newParagraph = recipient.createParagraph()
                    copyParagraph((XWPFParagraph) bodyElement, newParagraph, false, minLeftIndent)
                }
            } else if(bodyElement.getElementType().name().equals("TABLE")) {
                copyTable(recipient, (XWPFTable)bodyElement)
            }
        }
    }

    private void copyParagraph(XWPFParagraph oldParagraph, XWPFParagraph newParagraph, boolean firstParagraph, int minLeftIndent) {

        for (XWPFRun run : oldParagraph.getRuns()) {

            if ((run.getText(0) != null && !run.getText(0).isEmpty()) || (run.getEmbeddedPictures() != null && run.getEmbeddedPictures().size() > 0)) {

                XWPFRun newRun = newParagraph.createRun()

                if (run.getText(0) != null && !run.getText(0).isEmpty()) {
                    newRun.setText(run.getText(0))
                    if (run.getFontSize() != -1) {
                        newRun.setFontSize(run.getFontSize())
                    }
                    newRun.setFontFamily(run.getFontFamily())
                    newRun.setBold(run.isBold())
                    newRun.setItalic(run.isItalic())
                    newRun.setStrike(run.isStrike())
                    newRun.setColor(run.getColor())
                    newRun.setUnderline(run.getUnderline())
                    newRun.setSubscript(run.getSubscript())
                    newRun.setTextPosition(run.getTextPosition())
                }
                if (run.getEmbeddedPictures() != null && run.getEmbeddedPictures().size() > 0) {
                    for (XWPFPicture pic : run.getEmbeddedPictures()) {

                        byte[] img = pic.getPictureData().getData()
                        long cx = pic.getCTPicture().getSpPr().getXfrm().getExt().getCx()
                        long cy = pic.getCTPicture().getSpPr().getXfrm().getExt().getCy()
                        int pictureType = pic.getPictureData().getPictureType()

                        XWPFDocument document = newParagraph.getDocument()

                        String blipId = document.addPictureData(new ByteArrayInputStream(img), pictureType)
                        createPictureCxCy(document, blipId, document.getNextPicNameNumber(pictureType), cx, cy)
                    }
                }
            }
        }

        for (CTOMathPara ctoMathPara : oldParagraph.getCTP().OMathParaList) {
            newParagraph.getCTP().OMathParaList.add(ctoMathPara)
        }

        if (!firstParagraph) {
            newParagraph.setIndentationFirstLine(oldParagraph.getIndentationFirstLine())
        }

        newParagraph.setAlignment(oldParagraph.getAlignment())
        newParagraph.setSpacingLineRule(oldParagraph.getSpacingLineRule())
        newParagraph.setVerticalAlignment(oldParagraph.getVerticalAlignment())

        if (oldParagraph.getSpacingAfter() != -1) newParagraph.setSpacingAfter(oldParagraph.getSpacingAfter())
        if (oldParagraph.getSpacingBefore() != -1) newParagraph.setSpacingBefore(oldParagraph.getSpacingBefore())
        if (oldParagraph.getIndentationRight() != -1) newParagraph.setIndentationRight(oldParagraph.getIndentationRight())
        if (oldParagraph.getSpacingAfterLines() != -1) newParagraph.setSpacingAfterLines(oldParagraph.getSpacingAfterLines())
        if (oldParagraph.getSpacingBeforeLines() != -1) newParagraph.setSpacingBeforeLines(oldParagraph.getSpacingBeforeLines())
        if (oldParagraph.getIndentationHanging() != -1) newParagraph.setIndentationHanging(oldParagraph.getIndentationHanging())

        if (!firstParagraph && oldParagraph.getIndentationLeft() != -1) {
            if(oldParagraph.getIndentationLeft() < 0) {
                newParagraph.setIndentationLeft(oldParagraph.getIndentationLeft() - minLeftIndent)
            } else {
                newParagraph.setIndentationLeft(oldParagraph.getIndentationLeft())
            }
        }
    }

    private void copyTable(XWPFDocument recipient, XWPFTable table) {
        recipient.createTable()
        int pos = recipient.getTables().size() - 1

        CTTblPr tblPr = table.getCTTbl().getTblPr()
        CTJc jc = (tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc())
        jc.setVal(STJc.CENTER)

        recipient.setTable(pos, table)
    }

    private void createPictureCxCy(XWPFDocument recipient, String blipId,int id, long cx, long cy) {

        CTInline inline = recipient.createParagraph().createRun().getCTR().addNewDrawing().addNewInline()

        String picXml = "" +
                "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">" +
                "   <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">" +
                "      <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">" +
                "         <pic:nvPicPr>" +
                "            <pic:cNvPr id=\"" + id + "\" name=\"Generated\"/>" +
                "            <pic:cNvPicPr/>" +
                "         </pic:nvPicPr>" +
                "         <pic:blipFill>" +
                "            <a:blip r:embed=\"" + blipId + "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>" +
                "            <a:stretch>" +
                "               <a:fillRect/>" +
                "            </a:stretch>" +
                "         </pic:blipFill>" +
                "         <pic:spPr>" +
                "            <a:xfrm>" +
                "               <a:off x=\"0\" y=\"0\"/>" +
                "               <a:ext cx=\"" + cx + "\" cy=\"" + cy + "\"/>" +
                "            </a:xfrm>" +
                "            <a:prstGeom prst=\"rect\">" +
                "               <a:avLst/>" +
                "            </a:prstGeom>" +
                "         </pic:spPr>" +
                "      </pic:pic>" +
                "   </a:graphicData>" +
                "</a:graphic>"

        XmlToken xmlToken = null
        try {
            xmlToken = XmlToken.Factory.parse(picXml)
        }
        catch(XmlException xe) {
            xe.printStackTrace()
        }
        inline.set(xmlToken)

        inline.setDistT(0)
        inline.setDistB(0)
        inline.setDistL(0)
        inline.setDistR(0)

        CTPositiveSize2D extent = inline.addNewExtent()
        extent.setCx(cx)
        extent.setCy(cy)

        CTNonVisualDrawingProps docPr = inline.addNewDocPr()
        docPr.setId(id)
        docPr.setName("Picture " + id)
        docPr.setDescr("Generated")
    }
}