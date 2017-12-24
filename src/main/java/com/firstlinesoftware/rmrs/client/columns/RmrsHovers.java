package com.firstlinesoftware.rmrs.client.columns;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.columns.HoverDefinition;
import com.firstlinesoftware.base.client.columns.HoversFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.utils.hover.HoverBuilder;
import com.firstlinesoftware.base.client.utils.hover.Row;
import com.firstlinesoftware.base.client.utils.hover.SafeHtmlRow;
import com.firstlinesoftware.base.shared.dto.AttachedFile;
import com.firstlinesoftware.ecm.client.utils.EcmHoverBuilder;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview_imported.client.TreeTableContext;

/**
 * User: AKrutov
 * Date: 24.01.14
 * Time: 17:36
 */
public class RmrsHovers implements Registrable {
    private static final HoversFactory HOVERS_FACTORY = Base.getInjector().getHoversFactory();
    private static final RmrsMessages MESSAGES = Rmrs.getInjector().getMessages();
    public static final Template rmrsHoverTemplate = GWT.create(Template.class);
    private static boolean russian = true;
    private static boolean compact = true;

    public static boolean isRussian() {
        return russian;
    }

    public static boolean isCompact() {
        return compact;
    }

    public static void setLanguage(boolean isRussian) {
        RmrsHovers.russian = isRussian;
    }

    public static void setCompactMode(boolean mode) {
        RmrsHovers.compact = mode;
    }


    public interface Template extends SafeHtmlTemplates {
        @Template("<div class=\"hover_ hover_{2}\" style=\"position: relative; width: 100%; white-space:normal\"><div style=\"position: absolute; z-index: -32767; top: -20ex; width: 10em; height: 10ex;\">&nbsp;</div>{0}</div>")
        SafeHtml hover(SafeHtml contents, int height, int linesCount);

        @Template("<div class=\"hover_ hover_big\" style=\"position: relative; width: 100%\"><img src=\"{0}\" style=\"width: 100%; height: 100%\"/> </div>")
        SafeHtml pdf(String url);
    }

    @Override
    public void register() {
        HOVERS_FACTORY.register(requirementHover(), RequirementColumns.ROW_CLASS);
        HOVERS_FACTORY.register(gridRequirementHover(), RequirementColumns.GRID_ROW_CLASS);
    }

    private static HoverDefinition requirementHover() {
        return new HoverDefinition<Requirement>() {
            @Override
            public void render(TreeTableContext context, final Requirement req, final SafeHtmlBuilder sb) {
                if (compact) {
                    return;
                }
                final AttachedFile file = russian ? req.russian : req.english;
                if (file != null && file.id != null) {
                    sb.append(rmrsHoverTemplate.pdf(getImageSrc(file.id)));
                } else {
                    final EcmHoverBuilder builder = new MultilineHoverBuilder(this);
                    final String s = req.number != null ? "<b>" + req.number + "</b>&nbsp;" + req.getName() : req.getName();
                    builder.add(new SafeHtmlRow(s != null ? SafeHtmlUtils.fromTrustedString(s) : SafeHtmlUtils.EMPTY_SAFE_HTML));
                    sb.append(builder.build());
                }
            }

            @Override
            public int getLinesCount() {
                return 5;
            }

        };
    }

    private static HoverDefinition<Requirement> gridRequirementHover() {
        return new HoverDefinition<Requirement>() {
            @Override
            public void render(TreeTableContext context, final Requirement value, final SafeHtmlBuilder sb) {
                final EcmHoverBuilder builder = new EcmHoverBuilder(this);
                builder.addTitle(value.getTitle(), value.id)
                        .startOneColumn(100)
                        .addPosition(MESSAGES.responsible(), value.responsible);
                sb.append(builder.build());
            }

            @Override
            public int getLinesCount() {
                return 2;
            }
        };
    }


    private static String getImageSrc(String documentId) {
        return GWT.getHostPageBaseURL() + "view/" + documentId + "?ticket=" + Dictionary.getDictionary("authData").get("ticket") + "&pageNum=100500";
    }


    private static class MultilineHoverBuilder extends EcmHoverBuilder {
        public MultilineHoverBuilder(HoverDefinition definition) {
            super(definition);
            setRowHeight(HoverBuilder.ROW_HEIGHT * 5);
        }

        @Override
        public SafeHtml build() {
            final SafeHtmlBuilder builder = new SafeHtmlBuilder();
            for (Row row : rows) {
                row.appendTo(builder);
            }
            return rmrsHoverTemplate.hover(builder.toSafeHtml(), countRowsHeight(5), 5);
        }
    }
}
