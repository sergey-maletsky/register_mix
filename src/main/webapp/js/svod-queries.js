var rscrHref = window.location.href,
    serverUrl = rscrHref.substring(0, rscrHref.lastIndexOf("/"));
var englishFiles = {};
var russianFiles = {};
var englishOrders = {};
var russianOrders = {};
var englishNumbers = {};
var russianNumbers = {};

var rusMinDate = {};
var rusMaxDate = {};
var enMinDate = {};
var enMaxDate = {};

var navigateEnglishFiles = {};
var navigateEnglishOrders = {};
var navigateRussianFiles = {};
var navigateRussianOrders = {};
var navigateEnglishNumbers = {};
var navigateRussianNumbers = {};

var navigateRusMinDate = {};
var navigateRusMaxDate = {};
var navigateEnMinDate = {};
var navigateEnMaxDate = {};

var dates = getDateFns();
var itemCount = 1;
var countWas = 0;
var nestedTreeElementIds = [];
var nestedCenterElementIds = [];
var nestedCenterElementValues = [];
var tempCenterElements = [];
var nestedElements = [];
var wasNested = false;


//THE ENTRY POINT
$(function() {
    addAuthorization(serverUrl);
    closeButtonBind();

    var formData = {
        "login" : "admin",
        "password" : "admin"
    };

    $.ajax({
        type: "POST",
        url: serverUrl + "/login",
        data: formData,
        dataType: 'text',
        success: function (ticketId) {
            $('#ticket_id').text(ticketId);
            searchByFolder(ticketId, "root_");
            //getUserData(ticketId);
            /*getTasks(ticketId);*/
        },
        error: function (jqXHR, exception) {
            if (jqXHR.status == '401' || jqXHR.status == '403') {
                document.getElementById("wrong_login").style.visibility = 'visible';
            } else if(jqXHR.status == '500') {
                document.getElementById("internal_error").style.visibility = 'visible';
            }
        }
    });
});
//END THE ENTRY POINT

//QUERIES
//get roots
function searchByFolder(ticketId, rootString) {
    var formData = {
        "ticket" : ticketId,
        "locale" : "ru",
        "folder" : "/",
        "showRecursive" : false,
        "onlyHeaders" : true
    };

    $.ajax({
        url: serverUrl + "/dispatch.rest/usecases/getByFolderWithAttachVersions?ticket=" + ticketId + "&locale=ru&folder=%2F&showRecursive=false&onlyHeaders=true",
        type: 'GET',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (result) {
            var count = 1;
            var imageCount = 1;
            if (result.dtos.length !== 0) {
                $("#req_number").text(result.dtos.length);
            } else {
                $("#req_number").text("");
            }
            tempCenterElements = result.dtos;
            tempCenterElements.sort(compareValues);
            $.each(tempCenterElements, function (key, value) {

                if (count == 1) {
                    addDescriptionLines(value, ticketId);
                }

                var rootNumber = rootString + count;
                leftTreeAppender(value, rootNumber);
                count++;
                treeItemBind(rootNumber, ticketId, value);

            });

            sortCenter(rootString);
        },
        error: function (ex) {
        }
    });
}


//The main documents search
function searchDocuments(ticketId, mustHaveJson, mustBeNullJson, isRoot, rootString, isDoubleCenterClick, isDoubleImageCenterClick, parentValue) {
    var formData = {
        "@class" : "com.firstlinesoftware.base.shared.dto.SearchCriteria",
        "type" : "rmrs:requirement",
        "folder" : null,
        "folders" : null,
        "content" : null,
        "orderBy" : null,
        "descending" : false,
        "limit" : 0,
        "maxPermissionChecks" : -1,
        "maxPermissionCheckTimeMillis" : -1,
        "mustHaveRanges" : {},
        "shouldHaveRanges" : {},
        "shouldHaveDateRanges" : [],
        "mustHaveDateRanges" : [],
        "shouldHave" : [],
        "mustHaveGroups" : [],
        "mustHave" : mustHaveJson,
        "shouldHaveGroups" : [],
        "mustNotHave" : [],
        "mustBeNull" : mustBeNullJson,
        "mustBeNotNull" : [],
        "shouldHaveDepartments" : [],
        "mustHaveDepartments": [],
        "shouldHaveAny" : [],
        "mustHaveAny" : [],
        "mustHaveAspect" : [],
        "mustNotHaveAspect" : [],
        "shouldHaveParent" : [],
        "mustHaveParent" : [],
        "mustNotHaveParent" : [],
        "id" : null
    };

    $.ajax({
        url: serverUrl + "/dispatch.rest/usecases/searchDocuments?ticket=" + ticketId + "&locale=ru",
        type: 'POST',
        data: JSON.stringify(formData),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (result) {
            var count = 1;
            var imageCount = 1;
            if (result.dtos.length !== 0) {
                $("#req_number").text(result.dtos.length);
            } else {
                $("#req_number").text("");
            }
            tempCenterElements = result.dtos;
            tempCenterElements.sort(compareValues);
            if(!jQuery.isEmptyObject(parentValue)) {
                tempCenterElements.unshift(parentValue);
                var parentId = parentValue.id;
            }
            $.each(tempCenterElements, function (key, value) {
                if (isRoot) {
                    if (count == 1) {
                        addDescriptionLines(value, ticketId);
                    }

                    var rootNumber = rootString + count;
                    leftTreeAppender(value, rootNumber);
                    count++;
                    treeItemBind(rootNumber, ticketId, value);
                } else if (isDoubleCenterClick){
                    centerTreeAppender(ticketId, value, imageCount, "center_tree", {});
                    fillImageArrays(value, russianFiles, englishFiles, rusMinDate, rusMaxDate, enMinDate, enMaxDate,
                        russianOrders, englishOrders, russianNumbers, englishNumbers);
                    imageCount++;
                } else if (isDoubleImageCenterClick){
                    /*if(value.russian) {
                     russianFiles[value.id] = value.russian.id;
                     }
                     if(value.english) {
                     englishFiles[value.id] = value.english.id;
                     }*/
                } else {
                    var rootNumber = rootString + "_" + count;
                    if (value.id !== parentId) {
                        var array = rootNumber.split("_").map(Number);
                        var subRoot = $("#left_tree").find("#" + rootString + "_i");
                        if (!subRoot.hasClass("level-over-hide")) {
                            $("#" + rootString).after(
                                "<div id='" + rootString + "_i' class='level-over-hide no-overflow '>" +
                                "</div>"
                            );
                        }

                        iElementAppender(value, rootString, rootNumber, array);
                        if (value.header && (value.leafHeader || value.isLeaf)) {
                            $("#" + rootNumber).find("img").attr("src", "");
                        }
                        /*                    setLeftTreeElement(
                         ticketId,
                         [{"first": "rmrs:parent", "second": value.id}, {"first" : "lifecycle", "second" : "routes_signed"}],
                         rootNumber
                         );*/
                        treeItemBind(rootNumber, ticketId, value);
                    }

                    if ($("#center_tree").hasClass("compact")) {
                        centerTreeAppender(ticketId, value, imageCount, "center_tree", parentId);
                    } else {
                        centerImageAppender();
                    }
                    count++;
                    imageCount++;
                }
            });

            sortCenter(rootString);
        },
        error: function (ex) {
        }
    });
}

function sortCenter(rootString) {
    var len;
    var nestedTreeElements = [],
        nestedCenterElements = [];

    nestedTreeElements.length = 0;
    nestedTreeElements = [];
    nestedCenterElements.length = 0;
    nestedCenterElements = [];
    for (ind = 0, len = nestedTreeElementIds.length; ind < len; ++ind) {
        nestedTreeElements.push($("#" + nestedTreeElementIds[ind]));
    }
    for (ind = 0, len = nestedCenterElementIds.length; ind < len; ++ind) {
        nestedCenterElements.push($("#" + nestedCenterElementIds[ind]));
    }


    for (ind = 0, len = nestedTreeElements.length; ind < len; ++ind) {
        var nested = nestedTreeElements[ind].attr("order");
        var n = nested;
    }

    nestedTreeElements.sort(compareElementsOrderNumber);
    nestedCenterElements.sort(compareElementsOrderNumber);

    $("#" + rootString + "_i").empty();
    for (ind = 0, len = nestedTreeElements.length; ind < len; ++ind) {
        $("#" + rootString + "_i").append(nestedTreeElements[ind]);
    }

    $("#center_tree").empty();
    for (ind = 0, len = nestedCenterElements.length; ind < len; ++ind) {
        $("#center_tree").append(nestedCenterElements[ind]);
    }

    nestedTreeElementIds.length = 0;
    nestedTreeElementIds = [];
    nestedCenterElementIds.length = 0;
    nestedCenterElementIds = [];
}

function compareElementsOrderNumber(a, b) {
    if (a.attr("order") && b.attr("order")) {
        return a.attr("order") - b.attr("order")
    }

    if (a.attr("number") && b.attr("number")) {
        return a.attr("number") - b.attr("number")
    }

    return 0;
}

function compareValues(a, b) {
    if (a.number > b.number) {
        return 1;
    }
    if (a.number < b.number) {
        return -1;
    }

    return 0;
}

//The search for changing center items during
//push compact and effective buttons processing
function simpleSearchDocuments(ticketId, mustHaveJson, mustBeNullJson, isNested) {
    var formData = {
        "@class" : "com.firstlinesoftware.base.shared.dto.SearchCriteria",
        "type" : "rmrs:requirement",
        "folder" : null,
        "folders" : null,
        "content" : null,
        "orderBy" : null,
        "descending" : false,
        "limit" : 0,
        "maxPermissionChecks" : -1,
        "maxPermissionCheckTimeMillis" : -1,
        "mustHaveRanges" : {},
        "shouldHaveRanges" : {},
        "shouldHaveDateRanges" : [],
        "mustHaveDateRanges" : [],
        "shouldHave" : [],
        "mustHaveGroups" : [],
        "mustHave" : mustHaveJson,
        "shouldHaveGroups" : [],
        "mustNotHave" : [],
        "mustBeNull" : mustBeNullJson,
        "mustBeNotNull" : [],
        "shouldHaveDepartments" : [],
        "mustHaveDepartments": [],
        "shouldHaveAny" : [],
        "mustHaveAny" : [],
        "mustHaveAspect" : [],
        "mustNotHaveAspect" : [],
        "shouldHaveParent" : [],
        "mustHaveParent" : [],
        "mustNotHaveParent" : [],
        "id" : null
    };

    $.ajax({
        url: serverUrl + "/dispatch.rest/usecases/searchDocuments?ticket=" + ticketId + "&locale=ru",
        type: 'POST',
        data: JSON.stringify(formData),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (result) {
            var centerElements = result.dtos;
            centerElements.sort(compareValues);
            if (isNested) {
                $.each(centerElements, function (key, value) {
                    itemCount++;
                    fillImageArrays(value, navigateRussianFiles, navigateEnglishFiles, navigateRusMinDate, navigateRusMaxDate, navigateEnMinDate, navigateEnMaxDate,
                        navigateRussianOrders, navigateEnglishOrders, navigateRussianNumbers, navigateEnglishNumbers);
                    //centerTreeAppender(ticketId, value, itemCount, "center_tree");
                    nestedCenterElementValues.push(value);
                    simpleSearchDocuments(ticketId,
                        [{"first": "rmrs:parent", "second": value.id}, {"first" : "lifecycle", "second" : "routes_signed"}],
                        [],
                        true
                    );
                });

                if (centerElements.length == 0 && wasNested == false || countWas != nestedCenterElementValues.length) {
                    countWas = itemCount;
                    wasNested = true;
                    $("#center_tree").empty();
                    sortNestedItems();
                }

                $("#req_number").text(itemCount);
            } else {
                if (result.dtos.length !== 0) {
                    $("#req_number").text(result.dtos.length);
                } else {
                    $("#req_number").text("");
                }

                var itemCountInternal = 1;
                tempCenterElements.length = 0;
                tempCenterElements = [];
                $.each(centerElements, function (key, value) {
                    tempCenterElements.push(value);
                    centerTreeAppender(ticketId, value, itemCountInternal, "center_tree", {});
                    itemCountInternal++;
                });
            }
            centerElements.length = 0;
            centerElements = [];
        },
        error: function (ex) {
        }
    });
}

//the search for the searching page
function simpleSearch(ticketId, mustHave, shouldHave, mustHaveAny) {
    var formData = {
        "@class" : "com.firstlinesoftware.base.shared.dto.SearchCriteria",
        "type" : null,
        "folder" : null,
        "folders" : null,
        "content" : null,
        "orderBy" : null,
        "descending" : false,
        "limit" : 0,
        "maxPermissionChecks" : -1,
        "maxPermissionCheckTimeMillis" : -1,
        "mustHaveRanges" : {},
        "shouldHaveRanges" : {},
        "shouldHaveDateRanges" : [],
        "mustHaveDateRanges" : [],
        "shouldHave" : shouldHave,
        "mustHaveGroups" : [],
        "mustHave" : mustHave,
        "shouldHaveGroups" : [],
        "mustNotHave" : [],
        "mustBeNull" : [],
        "mustBeNotNull" : [],
        "shouldHaveDepartments" : [],
        "mustHaveDepartments": [],
        "shouldHaveAny" : [],
        "mustHaveAny" : mustHaveAny,
        "mustHaveAspect" : [],
        "mustNotHaveAspect" : [],
        "shouldHaveParent" : [],
        "mustHaveParent" : [],
        "mustNotHaveParent" : [],
        "id" : null
    };

    $.ajax({
        url: serverUrl + "/dispatch.rest/usecases/searchDocuments?ticket=" + ticketId + "&locale=ru",
        type: 'POST',
        data: JSON.stringify(formData),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (result) {
            $("#search_center_tree").removeClass("loading");
            $("#search_button").prop("disabled", false);
            $(".cancel_from_search").prop("disabled", false);
            $(".new_search").prop("disabled", false);
            $("#full_search").prop("disabled", false);
            var imageCount = 1;
            $.each(result.dtos, function (key, value) {
                centerTreeAppender(ticketId, value, imageCount, "search_center_tree", {});
                imageCount++;
            });

            if (result.dtos.length == 0 && $("#nothing_found").length == 0) {
                $("#search_center_tree").append("<div id='nothing_found' style='font-style: italic; text-align: center; color: #808080;'>Ничего не найдено</div>");
            }
        },
        error: function (ex) {
        }
    });
}

function getTasks(ticketId, authorId) {
    var formData = {
        "ticket" : ticketId,
        "locale" : "ru",
        "positionIds" : "58cfce62-ede7-4223-a038-021c4be25497",
        "excluded" : "errands_archive",
        "excluded" : "requirements.drafts",
        "excluded" : "proposal.drafts",
        "excluded" : "circular.drafts"
    };

    $.ajax({
        url: serverUrl + "/dispatch.rest/usecases/getTasks",
        type: 'GET',
        data: formData,
        dataType: 'json',
        success: function (result) {
            alert(result);
        },
        error: function (ex) {
            alert("getTasks error");
        }
    });

}

function getTask(ticketId, authorId, requirementId) {
    var formData = {
        "ticket" : ticketId,
        "locale" : "ru",
        "id" : requirementId,
        "positionIds" : "58cfce62-ede7-4223-a038-021c4be25497"
    };

    $.ajax({
        url: serverUrl + "/dispatch.rest/usecases/getTasks",
        type: 'GET',
        data: formData,
        dataType: 'json',
        success: function (result) {
            alert(result);
        },
        error: function (ex) {
            alert("getTask error");
        }
    });

}

function getParentDocument(ticketId, value, countLines) {
    var formData = {
        "ticket" : ticketId,
        "locale" : "ru",
        "id" : value.parent.id
    };

    $.ajax({
        url: serverUrl + "/dispatch.rest/usecases/getDocumentExt",
        type: 'GET',
        data: formData,
        dataType: 'json',
        success: function (result) {
            addIncludedIn(result, countLines);
        },
        error: function (ex) {
        }
    });

}

function getDocument(ticketId, requirementId, isCompact) {
    var formData = {
        "ticket" : ticketId,
        "locale" : "ru",
        "id" : requirementId
    };

    $.ajax({
        url: serverUrl + "/dispatch.rest/usecases/getDocumentExt",
        type: 'GET',
        data: formData,
        dataType: 'json',
        success: function (result) {
            if (isCompact) {
                addDescriptionLines(result.dto, ticketId);
            }
            if ("russian" in result.dto || "english" in result.dto) {
                var both = 25;
                if (result.dto.russian && result.dto.english) {
                    both = 48;
                }

                addFiles(both);
                baseFileTabBarAppender(result.dto, ticketId);
            }
        },
        error: function (ex) {
        }
    });
}

function getUserData(ticketId) {
    var formData = {
        "ticket" : ticketId,
        "locale" : "ru"
    };

    $.ajax({
        url: serverUrl + "/dispatch.rest/usecases/getClientInitInfo",
        type: 'GET',
        data: formData,
        dataType: 'json',
        success: function (result) {
            var userId = "";
            for (var key in result.dto.userProfile.roles) {
                if (key.length > 30) {
                    userId = key;
                }
            }

            searchDocuments(ticketId,
                [{"first" : "ecm:lyfecycle", "second" : "routes_signed"}, {"first" : "rmrs:responsible", "second" : userId}],
                [],
                true,
                "root_",
                false,
                false,
                {}
            );
        },
        error: function (ex) {
        }
    });
}

function getDocumentCounting(ticketId, requirementId) {
    var formData = {
        "ticket" : ticketId,
        "locale" : "ru",
        "id" : requirementId
    };

    $.ajax({
        url: serverUrl + "/dispatch.rest/usecases/getDocumentCounting",
        type: 'GET',
        data: formData,
        dataType: 'json',
        success: function (result) {
            if ($("#counting").length == 0) {
                $("#counting_place").append(
                    "<div id='counting' class='headerLabel document-counting' title='Количество посещений'>Посещений всего: " + result.string +
                    "</div>"
                );
            } else {
                $("#counting").text("Посещений всего: " + result.string);
            }
        },
        error: function (ex) {
        }
    });
}
//END QUERIES

//THE RIGHT PANEL
function baseFileTabBarAppender(value, ticketId) {
    var topSize = 45;
    if (value.english && value.russian) topSize = 60;
    if (value.russian) {
        var name = "";
        if (value.russian.name) {
            name = value.russian.name;
        } else {
            name = value.attachedFiles[0].name;
        }

        $("#fileContainer").append(
            "<div class='gwt-TabLayoutPanelTab' id='"+ value.russian.id + "' ticketid='" + ticketId + "' style='float: left;'>" +
            "<div class='gwt-TabLayoutPanelTabInner'>" +
            "<div class='gwt-Label'>" + name + "</div>" +
            "</div>" +
            "</div>"
        );
        fileBind(ticketId, value.russian.id, topSize, name);
    }
    if (value.english) {
        var name = "";
        if (value.english.name) {
            name = value.english.name;
        } else {
            name = value.attachedFiles[0].name;
        }

        $("#fileContainer").append(
            "<div class='gwt-TabLayoutPanelTab' id='"+ value.english.id + "' ticketid='" + ticketId + "' style='float: left;'>" +
            "<div class='gwt-TabLayoutPanelTabInner'>" +
            "<div class='gwt-Label'>" + name + "</div>" +
            "</div>" +
            "</div>"
        );
        fileBind(ticketId, value.english.id, topSize, name);
    }

    $(document).on("click", "#docSwitcher", function () {
        $("#docSwitcher").addClass("gwt-TabLayoutPanelTab-selected");
        $("#right_viewer").css({"top": ""});
        $("#right_viewer").css({"height": "0px"});
    });
}

function cleanPdfSearcher() {
    $("#findbar").addClass("hidden");
    $("#findInput").removeClass('notFound');
    $("#findInput").val("");
    $("#findResultsCount").addClass("hidden");
}

function fileBind(ticketId, imageId, topSize, name) {
    $(document).on("click", "#" + imageId + "", function (e) {
        e.preventDefault();
        $("#viewer_file_name").text(name);
        $("#right_viewer").css({"top": topSize + "px"});
        $("#right_panel_description_top").css({"top": topSize + "px"});
        $("#right_viewer").css({"height": ""});
        $("#docSwitcher").removeClass("gwt-TabLayoutPanelTab-selected");
        $("#pageContainer1").remove();
        cleanPdfSearcher();

        addImage(ticketId, imageId);
    });
}

function addFiles(both) {
    $("#right_panel_document").after(
        "<div class='file_delimiter' style='position: absolute; overflow: hidden; left: 90px; top: 2px; width: 5px; height: "+ both +"px;'>" +
        "<div class='base-tabsDelimiter zero-position--absolute'>" +
        "</div>" +
        "</div>" +
        "<div class='right_panel_files' style='position: absolute; overflow: hidden; left: 95px; top: 0px; right: 0px; height: " + (both + 17) + "px;'>" +
        "<div class='base-fileTabBar' style='position: relative; left: 0px; top: 0px; right: 0px; bottom: 0px; min-width: 333px;'>" +
        "<div id='fileContainer' style='margin-right: 60px; overflow-y: auto; max-height: 92px;'>" +
        "<div style='clear: both;'></div>" +
        "</div>" +
        "</div>" +
        "</div>"
    );
}

function addDescription(value) {
    addDocument();
    addDescriptionTop(value);
    $("#right_panel_description").append(
        "<div id='right_panel_top' style='position: absolute; left: 0px; right: 0px; top: 0%; bottom: 70%;'></div>" +
        "<div id='description_lines' style='position: absolute; left: 0px; right: 0px; top: 30%; bottom: 0%;'></div>"
    );
}

function addDocument() {
    $("#right_panel_lines").prepend(
        "<div id='right_panel_document' style='position: absolute; overflow: hidden; left: 0px; top: 0px; height: 65px; width: 90px;'>" +
        "<div class='base-tabBar' style='position: relative; left: 0px; top: 0px; right: 0px; bottom: 0px;'>" +
        "<div id='docSwitcher' class='gwt-TabLayoutPanelTab gwt-TabLayoutPanelTab-selected' style='float: left;'>" +
        "<div class='gwt-TabLayoutPanelTabInner'>" +
        "<div class='gwt-Label'>Документ</div>" +
        "</div>" +
        "</div>" +
        "<div style='clear: both;'></div>" +
        "</div>" +
        "</div>"
    );
}

function addDescriptionTop(value) {
    var topSize = 35;
    if (value.russian && value.english) {
        topSize = 60;
    }
    $("#right_panel_document").after(
        "<div id='right_panel_description_top' style='position: absolute; overflow: hidden; left: 0px; top: " + topSize + "px; right: 0px; bottom: 0px;'>" +
        "<div id='right_panel_description' class='form-Layout gwt-TabLayoutPanelContent' style='right: 0px; bottom: 0px; position: relative; left: 0px; top: 0px;'>" +
        "</div>" +
        "</div>"
    );
    $().css
}

function addDescriptionLines(value, ticketId) {
    $("#right_panel_document").remove();
    $("#right_panel_description_top").remove();
    $("#right_panel_top").empty();
    $("#description_lines").empty();
    $(".file_delimiter").remove();
    $(".right_panel_files").remove();
    $("#center_title").remove();
    //getDocumentCounting(ticketId, value.id);

    addCenterTitle(value);
    addDescription(value);
    var countHeader = 0;
    var countLines = 0;
    if (value.number) {
        addDescriptionNumber(value, countHeader);
        countHeader++;
    }
    if (value.name) {
        addDescriptionName(value, countHeader);
    }
    if (value.parent) {
        getParentDocument(ticketId, value, countLines);
        countLines++;
    }
    if (value.header) {
        addHeader(countLines);
        countLines++;
    }
    if (value.responsible && value.responsible.person.name !== value.author.person.name) {
        addResponsible(value, countLines);
        countLines++;
    }
    /*    if (value.created) {
     addCreationDate(value, countLines);
     countLines++;
     }
     if (value.author) {
     addAuthor(value, countLines);
     }*/
}

function addDescriptionNumber(value, count) {
    $("#right_panel_top").append(
        "<div class='form-ItemRow' style='position: absolute; left: 0px; right: 0px; top: " + count*28 + "px; bottom: 3px; height: 22px;'>" +
        "<div style='position: absolute; overflow: hidden; left: 0px; top: 0px; bottom: 0px; width: 96px;'>" +
        "<div class='form-Label zero-position--absolute' style='white-space: nowrap;'>" +
        "<nobr>Номер:</nobr>" +
        "</div>" +
        "</div>" +
        "<div style='position: absolute; overflow: hidden; left: 101px; top: 0px; right: 0px; bottom: 0px;'>" +
        "<div class='gwt-Label form-Item zero-position--absolute'>" + value.number +
        "</div>" +
        "</div>" +
        "</div>"
    );
}

function addDescriptionName(value, count) {
    $("#right_panel_top").append(
        "<div class='form-ItemRow' style='position: absolute; left: 0px; right: 0px; top: " + count*28 + "px; bottom: 3px;'>" +
        "<div style='position: absolute; overflow: hidden; left: 0px; top: 0px; bottom: 0px; width: 96px;'>" +
        "<div class='form-Label zero-position--absolute' style='white-space: nowrap;'>" +
        "<nobr>Наименование:</nobr>" +
        "</div>" +
        "</div>" +
        "<div style='position: absolute; overflow: hidden; left: 101px; top: 0px; right: 0px; bottom: 0px;'>" +
        "<div class='form-TextAreaLabel form-Item zero-position--absolute'>" + value.name +
        "</div>" +
        "</div>" +
        "</div>"
    );
}
function addIncludedIn(result, count) {
    $("#description_lines").append(
        "<div class='form-ItemRow' style='position: absolute; left: 0px; right: 0px; top: " + count*28 + "px; height: 22px;'>" +
        "<div style='position: absolute; overflow: hidden; left: 0px; top: 0px; bottom: 0px; width: 96px;'>" +
        "<div class='form-Label zero-position--absolute' style='white-space: nowrap;'><nobr>Включено в:</nobr>" +
        "</div>" +
        "</div>" +
        "<div style='position: absolute; overflow: hidden; left: 101px; top: 0px; right: 0px; bottom: 0px;'>" +
        "<a class='gwt-Label form-Item zero-position--absolute' href='#view:id=" + result.dto.id + "' style='overflow: hidden;'>" + result.dto.name +
        "</a>" +
        "</div>" +
        "</div>"
    );
}

function addHeader(count) {
    $("#description_lines").append(
        "<div class='form-ItemRow' style='position: absolute; left: 0px; right: 0px; top: " + count*28 + "px; height: 22px;'>" +
        "<div style='position: absolute; overflow: hidden; left: 0px; top: 0px; bottom: 0px; width: 96px;'>" +
        "<div class='form-Label zero-position--absolute' style='white-space: nowrap;'><nobr>Заголовок:</nobr>" +
        "</div>" +
        "</div>" +
        "<div style='position: absolute; overflow: hidden; left: 101px; top: 0px; right: 0px; bottom: 0px;'>" +
        "<span class='gwt-CheckBox gwt-CheckBox-disabled form-Item zero-position--absolute no-overflow'>" +
        "<input type='checkbox' value='on' tabindex='0' disabled='' name='com.firstlinesoftware.rmrs.shared.dto.Requirement_Заголовок' checked=''>" +
        "</span>" +
        "</div>" +
        "</div>"
    );
}

function addResponsible(value, count) {
    $("#description_lines").append(
        "<div class='form-ItemRow' style='position: absolute; left: 0px; right: 0px; top: " + count*28 + "px; height: 22px;'>" +
        "<div style='position: absolute; overflow: hidden; left: 0px; top: 0px; bottom: 0px; width: 96px;'>" +
        "<div class='form-Label zero-position--absolute' style='white-space: nowrap;'><nobr>Ответственный:</nobr>" +
        "</div>" +
        "</div>" +
        "<div style='position: absolute; overflow: hidden; left: 101px; top: 0px; right: 0px; bottom: 0px;'>" +
        "<div class='gwt-Label form-Item zero-position--absolute no-overflow'>" + value.responsible.person.name +
        "<span class='grayed'>(" + value.responsible.name + " / " + value.responsible.department.name + ")</span>" +
        "</div>" +
        "</div>" +
        "</div>"
    );
}

function addCreationDate(value, count) {
    var date = new Date(value.created);
    var day = date.getDate();
    var month = (date.getMonth() + 1);
    if (day < 10) { day = "0" + day}
    if (month < 10) { month = "0" + month}
    $("#description_lines").append(
        "<div class='form-ItemRow' style='position: absolute; left: 0px; right: 0px; top: " + count*28 + "px; height: 22px;'>" +
        "<div style='position: absolute; overflow: hidden; left: 0px; top: 0px; bottom: 0px; width: 96px;'>" +
        "<div class='form-Label zero-position--absolute' style='white-space: nowrap;'><nobr>Дата создания:</nobr>" +
        "</div>" +
        "</div>" +
        "<div style='position: absolute; overflow: hidden; left: 101px; top: 0px; right: 0px; bottom: 0px;'>" +
        "<div class='gwt-Label form-Item zero-position--absolute no-overflow'>" +
        day + "." + month + "." + date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes() +
        "</div>" +
        "</div>" +
        "</div>"
    );
}

function addAuthor(value, count) {
    $("#description_lines").append(
        "<div class='form-ItemRow' style='position: absolute; left: 0px; right: 0px; top: " + count*28 + "px; height: 22px;'>" +
        "<div style='position: absolute; overflow: hidden; left: 0px; top: 0px; bottom: 0px; width: 96px;'>" +
        "<div class='form-Label zero-position--absolute' style='white-space: nowrap;'><nobr>Автор:</nobr>" +
        "</div>" +
        "</div>" +
        "<div style='position: absolute; overflow: hidden; left: 101px; top: 0px; right: 0px; bottom: 0px;'>" +
        "<div class='gwt-Label form-Item zero-position--absolute no-overflow'>" + value.author.person.name +
        "<span class='grayed'>(" + value.author.name + " / " + value.author.department.name + ")</span>" +
        "</div>" +
        "</div>" +
        "</div>"
    );
}
//END THE RIGHT PANEL

//THE LEFT PANEL
function leftTreeAppender(value, rootNumber) { //for root elements only
    if (!value.isLeaf && value.header) {
        var num = "";
        if (value.number) {
            num = value.number + " ";
        }
        $("#left_tree").append(
            "<div id='" + rootNumber + "' requirementid='" + value.id + "' style='padding-left: 0px;' class='cellTreeItem'>" +
            "<div style='padding-left: 16px;position:relative;' class='cellTreeItem cellTreeItemImageValue'> " +
            "<div class='cellTreeItemImage tree-item " + rootNumber + "_image'>" +
            "<img class='tree-item__image tree-item__image--plus i_0' src='images/plus.png'> " +
            "</div> " +
            "<div class='cellTreeItemValue'> " +
            "<span class='treeValueSpan tree-item__folder tree-item__folder--closed'> " + num + value.name + "</span> " +
            "</div> " +
            "</div> " +
            "</div>");
    }
}

function treeItemBind(rootNumber, ticketId, value) {
    var clickCount = 0;
    $(document).on("click", "." + rootNumber + "_image", function (e) {

    });

    $(document).on("click", "#" + rootNumber + "", function (e) {
        e.preventDefault();

        emptyFiles();
        cleanPdfSearcher();
        $("#navigate").attr("src", "images/buttons/24/navigate_down.png");
        if ($(".requirements_rel").is(":visible")) {
            setRightPanel();
            backSearchRightPanel();
        };
        $(".requirements_rel").hide();
        $("#center_panel").show();
        var requirementId = $(this).attr("requirementid");
        $('#current_requirement_id').text(requirementId);
        var img = $(this).find("img");
        var isToggle = false;
        if (img.hasClass("i_" + clickCount)) {
            isToggle = true;
        }
        clickCount++;
        if (isToggle) {
            addDescriptionLines(value, ticketId);
            var subRoot = $("#left_tree").find("#" + rootNumber + "_i");
            $("#" + rootNumber + "_i").remove();
            $("#center_tree").empty();
            $(".base-fileTabBar").empty();

            $("#right_viewer").css({"top": ""});
            $("#right_viewer").css({"height": "0px"});
            getDocument(ticketId, requirementId, $("#center_tree").hasClass("compact")); //$("#center_tree").hasClass("compact")

            if (subRoot.length == 0) {
                searchDocuments(ticketId,
                    [{"first": "rmrs:parent", "second": requirementId}, {"first" : "lifecycle", "second" : "routes_signed"}],
                    [],
                    false,
                    rootNumber,
                    false,
                    false,
                    value
                );
            }

            toggleTreeItem(this, clickCount);
        }
    });
}

function emptyFiles() {
    russianFiles.length = 0;
    russianFiles = {};
    englishFiles.length = 0;
    englishFiles = {};

    englishOrders.length = 0;
    englishOrders = {};
    englishNumbers.length = 0;
    englishNumbers = {};
    russianOrders.length = 0;
    russianOrders = {};
    russianNumbers.length = 0;
    russianNumbers = {};
}

function emptyNavigateFiles() {
    navigateEnglishFiles.length = 0;
    navigateEnglishFiles = {};
    navigateRussianFiles.length = 0;
    navigateRussianFiles = {};

    navigateEnglishOrders.length = 0;
    navigateEnglishOrders = {};
    navigateEnglishNumbers.length = 0;
    navigateEnglishNumbers = {};
    navigateRussianOrders.length = 0;
    navigateRussianOrders = {};
    navigateRussianNumbers.length = 0;
    navigateRussianNumbers = {};
}

function iElementAppender(value, rootString, rootNumber, array) {
    if (value.header) {
        var num = "";
        if (value.number) {
            num = value.number + " ";
        }
        $("#" + rootString + "_i").append(
            "<div id='" + rootNumber + "' number='" + value.number + "' order='" + value.order + "' requirementid='" + value.id + "' style='padding-left:" + ((array.length - 2) * 16) + "px;' class='cellTreeItem'>" +
            "<div style='padding-left: 16px;position:relative;' class='cellTreeItem cellTreeItemImageValue'> " +
            "<div class='cellTreeItemImage tree-item " + rootNumber + "_image'>" +
            "<img class='tree-item__image tree-item__image--plus i_0' src='images/plus.png'> " +
            "</div> " +
            "<div class='cellTreeItemValue'> " +
            "<span class='treeValueSpan tree-item__folder tree-item__folder--closed'> " + num + value.name + "</span> " +
            "</div> " +
            "</div> " +
            "</div>");
    }

    nestedTreeElementIds.push(rootNumber);

    fillImageArrays(value, russianFiles, englishFiles, rusMinDate, rusMaxDate, enMinDate, enMaxDate,
        russianOrders, englishOrders, russianNumbers, englishNumbers);
}

function fillImageArrays(value, russianArray, englishArray, russianMinDate, russianMaxDate, englishMinDate, englishMaxDate,
                         russianOrders, englishOrders, russianNumbers, englishNumbers) {
    if(value.russian) {
        russianArray[value.id] = value.russian.id;
        russianOrders[value.id] = value.order;
        russianNumbers[value.id] = value.number;
        if (value.effective) {
            if (value.effective.min && value.effective.max) {
                russianMinDate[value.id] = value.effective.min;
                russianMaxDate[value.id] = value.effective.max;
            } else if (value.effective.min) {
                russianMinDate[value.id] = value.effective.min;
                russianMaxDate[value.id] = 0;
            } else if (value.effective.max) {
                russianMinDate[value.id] = 0;
                russianMaxDate[value.id] = value.effective.max;
            } else {
                russianMinDate[value.id] = 0;
                russianMaxDate[value.id] = 0;
            }
        } else {
            russianMinDate[value.id] = 0;
            russianMaxDate[value.id] = 0;
        }
    }
    if(value.english) {
        englishArray[value.id] = value.english.id;
        englishOrders[value.id] = value.order;
        englishNumbers[value.id] = value.number;
        if (value.effective) {
            if (value.effective.min && value.effective.max) {
                englishMinDate[value.id] = value.effective.min;
                englishMaxDate[value.id] = value.effective.max;
            } else if (value.effective.min) {
                englishMinDate[value.id] = value.effective.min;
                englishMaxDate[value.id] = 0;
            } else if (value.effective.max) {
                englishMinDate[value.id] = 0;
                englishMaxDate[value.id] = value.effective.max;
            } else {
                englishMinDate[value.id] = 0;
                englishMaxDate[value.id] = 0;
            }
        } else {
            englishMinDate[value.id] = 0;
            englishMaxDate[value.id] = 0;
        }
    }
}
//END THE LEFT PANEL

//THE CENTER PANEL
function centerTreeAppender(ticketId, value, count, element, parentId) {
    var binder = "center_image_";
    if (element === "search_center_tree") {
        binder = "search_" + binder + count;
    } else {
        binder = binder + count;
    }
    if ($("#" + binder).attr("requirementid") === value.id) {
        return;
    }
    centerAppender(value, count, element, binder);
    centerItemBind(binder, ticketId, value, parentId);
}

function centerAppender(value, count, element, binder) {
    var pointmin = "";
    var pointmax = "";
    var effmin;
    var effmax;
    var daymin = "";
    var daymax = "";
    var monthmin = "";
    var monthmax = "";
    var yearmin = "";
    var yearmax = "";
    /*    var hoursmin = "";
     var hoursmax = "";
     var minutesmin = "";
     var minutesmax = "";*/

    var currentDate = new Date();
    var textColor = "black";

    if (value.effective) {
        if (value.effective.min) {
            pointmin = ".";
            effmin = new Date(value.effective.min);
            daymin = effmin.getDate();
            monthmin = (effmin.getMonth() + 1);
            yearmin = effmin.getFullYear();
            /*            hoursmin = effmin.getHours()
             minutesmin = effmin.getMinutes();*/
            if (daymin < 10) {
                daymin = "0" + daymin
            }
            if (monthmin < 10) {
                monthmin = "0" + monthmin
            }

            if (dates.compare(currentDate, effmin) === -1) {
                textColor = "#8ca9c7";
            }
        }
        if (value.effective.max) {
            pointmax = ".";
            effmax = new Date(value.effective.max);
            daymax = effmax.getDate();
            monthmax = (effmax.getMonth() + 1);
            yearmax = effmax.getFullYear();
            /*            hoursmax = effmin.getHours()
             minutesmax = effmin.getMinutes();*/
            if (daymax < 10) {
                daymax = "0" + daymax
            }
            if (monthmax < 10) {
                monthmax = "0" + monthmax
            }
            if (dates.compare(currentDate, effmax) === 1) {
                textColor = "#afafaf";
            }
        }
    }

    left3 = 50; right3 = 3;
    maxWidth = 2;

    if (binder.indexOf('search') + 1) {
        left2 = 32; right2 = 42;
        left3 = 53;
        left4 = 81; right4 = 1;
        maxWidth = 0;
    }

    isFile = true;
    material = "";
    if (value.header) {
        isFile = false;
        material = "";
    }

    firstColumn = 0;
    secondColumn = 42;
    thirdColumn = 88;
    if (element === "search_center_tree") {
        firstColumn += 5;
        secondColumn += 5
        thirdColumn += 5;
    }

    var checkRange = false;
    var src = $("#effective_changing").attr("src");
    if (value.effective) {
        if ((effmin || effmax) && !(src.indexOf('not_only') + 1)) {
            checkRange = true;
        }
    }
    var isInRange = false;
    if (checkRange) {
        if (effmin && effmax) {
            if (dates.inRange(currentDate, effmin, effmax)) {
                isInRange = true;
            }
        } else if (typeof effmin != 'undefined' && dates.compare(currentDate, effmin) === 1) {
            isInRange = true;
        } else if (typeof effmax != 'undefined' && dates.compare(currentDate, effmax) === -1) {
            isInRange = true;
        }
    }

    var number = "";
    if (value.number) {
        number = value.number;
    }

    if (!checkRange || isInRange) {
        $("#" + element).append(
            "<div id='" + binder + "' number='" + value.number + "' order='" + value.order +"' requirementid='" + value.id + "' isfile='" + isFile + "'>" +
            "<div style='position: relative; height:100%; color: " + textColor + "' class='cellTreeTableItem cellTreeTableTopItem effective'>" +
            "<div onclick='' style='outline:none; position:relative; left:0; width: 100%; height: 100%; white-space: nowrap; overflow: hidden;' class='cellTreeTableItemImageValue cellTreeTableTopItemImageValue'>" +
            "<div style='outline:none; margin-left: 15px; position:relative; right: 0' tabindex='0'>" +
            "<div class='noHover' style='position:relative;'>" +

            "<div style='position:absolute; height: 100%; left: 0; min-width:50%; right: 471px;'>" +
            "<div style='outline:none; position:absolute; white-space: nowrap; overflow: hidden; height: 100%; left: 0%; right: 52%' class='cellTreeTableItemValue'>" + value.name + "</div>" +
            "<div style='outline:none; position:absolute; white-space: nowrap; overflow: hidden; height: 100%; left: " + left3 + "%; right: " + right3 + "%' class='cellTreeTableItemValue'>" + number + "</div>" +
            "</div>" +

            "<div style='position:absolute; height: 100%; right:0%; max-width:50%; width: 471px;'>" +
            "<div style='outline:none; position:absolute; white-space: nowrap; overflow: hidden; height: 100%; width: 39%; left: " + firstColumn + "%;' class='cellTreeTableItemValue'><span class='textNoWrap'>" + daymin + pointmin + monthmin + pointmin + yearmin + /*" " + hoursmin + ":" + minutesmin +*/ "</span></div>" +
            "<div style='outline:none; position:absolute; white-space: nowrap; overflow: hidden; height: 100%; width: 42%; left: " + secondColumn + "%' class='cellTreeTableItemValue'>" + daymax + pointmax + monthmax + pointmax + yearmax + /*" " + hoursmax + ":" + minutesmax +*/ "</div>" +
            "<div style='outline:none; position:absolute; white-space: nowrap; overflow: hidden; height: 100%; width: 8%; left: " + thirdColumn + "%' class='cellTreeTableItemValue'><a href='#requirements:id=" + value.id + "'><span class='material-icons' style='font-size:18px;color: black;' title='Утверждено'>" + material + "</span></a></div>" +
            "</div>" +
            "</div>" +
            "</div>" +
            "</div>" +
            "</div>" +
            "</div>"
        );

        nestedCenterElementIds.push(binder);
    }
}

function centerItemBind(binder, ticketId, value, parentId) {
    $(document).on("click", "#" + binder, function (e) {
        e.preventDefault();
        cleanPdfSearcher();
        var requirementId = $(this).attr("requirementid");

        if (binder.indexOf('search') + 1) {
            setSearchRightPanel();
        }
        addDescriptionLines(value, ticketId);
        $("#right_dragger").removeClass("disabled_dragger");
        $(".base-fileTabBar").empty();
        $("#right_viewer").css({"top": ""});
        $("#right_viewer").css({"height": "0px"});
        getDocument(ticketId, requirementId, false); //$("#center_tree").hasClass("compact")
    });

    $(document).one("dblclick", "#" + binder, function (e) {
        e.preventDefault();
        emptyFiles();
        $("#navigate").attr("src", "images/buttons/24/navigate_down.png");
        var requirementId = $(this).attr("requirementid");
        $('#current_requirement_id').text(requirementId);
        var isFile = $(this).attr("isfile");
        var isFiles = $("#center_tree").html() == "";

        if (isFile === "false" && !isFiles) {
            var val = value;
            if(!jQuery.isEmptyObject(parentId) && value.id !== parentId) {
                val = {};
            }
            $("#center_tree").empty();
            searchDocuments(ticketId,
                [{"first": "rmrs:parent", "second": requirementId}, {"first" : "lifecycle", "second" : "routes_signed"}],
                [],
                false,
                "",
                true,
                false,
                val
            );
        }
    });
}

function centerBack(requirementId) {
    var ticketId = $('#ticket_id').text();
    simpleSearchDocuments(ticketId,
        [{"first": "rmrs:parent", "second": requirementId}, {"first" : "lifecycle", "second" : "routes_signed"}],
        [],
        false
    );

    getDocument(ticketId, requirementId, true); //$("#center_tree").hasClass("compact")
}

function centerAddAllItems(requirementId) {
    var ticketId = $('#ticket_id').text();
    itemCount = 0;
    emptyNavigateFiles();
    wasNested = false;
    nestedCenterElementValues.length = 0;
    nestedCenterElementValues = [];
    simpleSearchDocuments(ticketId,
        [{"first": "rmrs:parent", "second": requirementId}, {"first" : "lifecycle", "second" : "routes_signed"}],
        [],
        true
    );

    getDocument(ticketId, requirementId, true);
}

function sortEngine(rootNumber, nestedElements) {
    var i, len;
    var isAdded = false;
    for (i = 0; i < nestedCenterElementValues.length; ++i) {
        var internalElementNumber = nestedCenterElementValues[i].number;
        if (rootNumber != internalElementNumber) {
            var item = nestedCenterElementValues[i];
            var number = rootNumber + ".";

            var part;
            if (internalElementNumber.length > number.length) {
                var t = internalElementNumber.substr(0, number.length);
                part = internalElementNumber.substr(0, number.length).indexOf(number);
            } else {
                part = internalElementNumber.indexOf(number);
            }

            if (internalElementNumber && part > -1) {
                var isNotNeedToAdd = false;
                for (var k = 0; k < nestedElements.length; k++) {
                    if (nestedElements[k].number == internalElementNumber) {
                        isNotNeedToAdd = true;
                    }
                }
                if (!isNotNeedToAdd) {
                    isAdded = true;
                    nestedElements.push(nestedCenterElementValues[i]);
                }
            }

            if (isAdded) {
                isAdded = false;
                sortEngine(internalElementNumber, nestedElements);
            }
        }
    }
}

function sortNestedItems() {
    var nestedElements = [],
        nestedElements = [];

    nestedElements.length = 0;
    nestedElements = [];

    var ticketId = $('#ticket_id').text();
    var i, z;

    tempCenterElements.sort(compareValues);
    for (z = 0; z < tempCenterElements.length; z++) {

        var isNotNeedToAdd = false;
        for (var k = 0; k < nestedElements.length; k++) {
            if (nestedElements[k].number == tempCenterElements[z].number) {
                isNotNeedToAdd = true;
            }
        }
        if (!isNotNeedToAdd) {
            nestedElements.push(tempCenterElements[z]);
            sortEngine(tempCenterElements[z].number, nestedElements);
        }
    }

    var f, g, h;
    for (f = 0; f < nestedElements.length; ++f) {
        for (g = f + 1; g < nestedElements.length; ++g) {
            if (nestedElements[f] !== null && (nestedElements[f].number == nestedElements[g].number)) {
                nestedElements[g] == null;
            }
        }
    }

    for (h = 0; h < nestedElements.length; ++h) {
        if (nestedElements[f] !== null) {
            centerTreeAppender(ticketId, nestedElements[h], h, "center_tree", {});
        }
    }
}

function centerImageAppender() {
    //imagesSort();
    //navImagesSort();
    $("#center_tree").empty();
    var src = $("#lang_changing").attr("src");
    var navSrc = $("#navigate").attr("src");
    if (navSrc.indexOf('up') + 1) {
        if (src.indexOf('eng') + 1) {
            navRusImagesSort();
            addFile(navigateRussianFiles, navigateRusMinDate, navigateRusMaxDate, "ru");
        } else {
            navEnImagesSort();
            addFile(navigateEnglishFiles, navigateEnMinDate, navigateEnMaxDate, "en");
        }
    } else {
        if (src.indexOf('eng') + 1) {
            rusImagesSort();
            addFile(russianFiles, rusMinDate, rusMaxDate, "ru");
        } else {
            enImagesSort();
            addFile(englishFiles, enMinDate, enMaxDate, "en");
        }
    }
}

function navRusImagesSort() {
    var keyArray = [];
    var valueArray = [];
    var resultFiles = {};
    //sortingFiles(navigateRussianOrders, navigateRussianNumbers, navigateRussianFiles);

    if (checkArray(navigateRussianOrders)) {
        for (var key in navigateRussianOrders) {
            keyArray.push(key);
            valueArray.push(navigateRussianOrders[key]);
        }
        for (var i = 0; i < valueArray.length; i++) {
            var valMin = valueArray[i];
            var keyMin = keyArray[i];
            for (var j = i + 1; j < valueArray.length + 1; j++) {
                if (valueArray[j] < valMin) {
                    var valTemp = valueArray[i];
                    var keyTemp = keyArray[i];
                    valMin = valueArray[j];
                    keyMin = keyArray[j];
                    valueArray[i] = valMin;
                    keyArray[i] = keyMin;
                    valueArray[j] = valTemp;
                    keyArray[j] = keyTemp;
                }
            }
        }

        for (var i = 0; i < keyArray.length; i++) {
            resultFiles[keyArray[i]] = russianFiles[keyArray[i]];
        }

        russianFiles = resultFiles;

    } else {
        for (var key in navigateRussianNumbers) {
            keyArray.push(key);
            valueArray.push(navigateRussianNumbers[key]);
        }
        for (var i = 0; i < valueArray.length; i++) {
            var valMin = valueArray[i];
            var keyMin = keyArray[i];
            for (var j = i + 1; j < valueArray.length + 1; j++) {
                if (valueArray[j] < valMin) {
                    var valTemp = valueArray[i];
                    var keyTemp = keyArray[i];
                    valMin = valueArray[j];
                    keyMin = keyArray[j];
                    valueArray[i] = valMin;
                    keyArray[i] = keyMin;
                    valueArray[j] = valTemp;
                    keyArray[j] = keyTemp;
                }
            }
        }

        for (var i = 0; i < keyArray.length; i++) {
            resultFiles[keyArray[i]] = navigateRussianFiles[keyArray[i]];
        }

        navigateRussianFiles = resultFiles;
    }
}

function navEnImagesSort() {
    //sortingFiles(navigateEnglishOrders, navigateEnglishNumbers, navigateEnglishFiles);
    var keyArray = [];
    var valueArray = [];
    var resultFiles = {};
    if (checkArray(navigateEnglishOrders)) {
        for (var key in navigateEnglishOrders) {
            keyArray.push(key);
            valueArray.push(navigateEnglishOrders[key]);
        }
        for (var i = 0; i < valueArray.length; i++) {
            var valMin = valueArray[i];
            var keyMin = keyArray[i];
            for (var j = i + 1; j < valueArray.length + 1; j++) {
                if (valueArray[j] < valMin) {
                    var valTemp = valueArray[i];
                    var keyTemp = keyArray[i];
                    valMin = valueArray[j];
                    keyMin = keyArray[j];
                    valueArray[i] = valMin;
                    keyArray[i] = keyMin;
                    valueArray[j] = valTemp;
                    keyArray[j] = keyTemp;
                }
            }
        }

        for (var i = 0; i < keyArray.length; i++) {
            resultFiles[keyArray[i]] = navigateEnglishFiles[keyArray[i]];
        }

        navigateEnglishFiles = resultFiles;
    } else {
        for (var key in navigateEnglishNumbers) {
            keyArray.push(key);
            valueArray.push(navigateEnglishNumbers[key]);
        }
        for (var i = 0; i < valueArray.length; i++) {
            var valMin = valueArray[i];
            var keyMin = keyArray[i];
            for (var j = i + 1; j < valueArray.length + 1; j++) {
                if (valueArray[j] < valMin) {
                    var valTemp = valueArray[i];
                    var keyTemp = keyArray[i];
                    valMin = valueArray[j];
                    keyMin = keyArray[j];
                    valueArray[i] = valMin;
                    keyArray[i] = keyMin;
                    valueArray[j] = valTemp;
                    keyArray[j] = keyTemp;
                }
            }
        }

        for (var i = 0; i < keyArray.length; i++) {
            resultFiles[keyArray[i]] = navigateEnglishFiles[keyArray[i]];
        }

        navigateEnglishFiles = resultFiles;
    }
}

function rusImagesSort() {
    //Russian files sort
    //sortingFiles(russianOrders, russianNumbers, russianFiles);
    var keyArray = [];
    var valueArray = [];
    var resultFiles = {};
    if (checkArray(russianOrders)) {
        for (var key in russianOrders) {
            keyArray.push(key);
            valueArray.push(russianOrders[key]);
        }
        for (var i = 0; i < valueArray.length; i++) {
            var valMin = valueArray[i];
            var keyMin = keyArray[i];
            for (var j = i + 1; j < valueArray.length + 1; j++) {
                if (valueArray[j] < valMin) {
                    var valTemp = valueArray[i];
                    var keyTemp = keyArray[i];
                    valMin = valueArray[j];
                    keyMin = keyArray[j];
                    valueArray[i] = valMin;
                    keyArray[i] = keyMin;
                    valueArray[j] = valTemp;
                    keyArray[j] = keyTemp;
                }
            }
        }

        for (var i = 0; i < keyArray.length; i++) {
            resultFiles[keyArray[i]] = russianFiles[keyArray[i]];
        }

        russianFiles = resultFiles;

    } else {
        for (var key in russianNumbers) {
            keyArray.push(key);
            valueArray.push(russianNumbers[key]);
        }
        for (var i = 0; i < valueArray.length; i++) {
            var valMin = valueArray[i];
            var keyMin = keyArray[i];
            for (var j = i + 1; j < valueArray.length + 1; j++) {
                if (valueArray[j] < valMin) {
                    var valTemp = valueArray[i];
                    var keyTemp = keyArray[i];
                    valMin = valueArray[j];
                    keyMin = keyArray[j];
                    valueArray[i] = valMin;
                    keyArray[i] = keyMin;
                    valueArray[j] = valTemp;
                    keyArray[j] = keyTemp;
                }
            }
        }

        for (var i = 0; i < keyArray.length; i++) {
            resultFiles[keyArray[i]] = russianFiles[keyArray[i]];
        }

        russianFiles = resultFiles;
    }

    /*    sortingFiles(russianFiles);
     sortingFiles(englishFiles);
     sortingFiles(navigateEnglishFiles);
     sortingFiles(navigateRussianFiles);*/
}

function enImagesSort() {
    //sortingFiles(englishOrders, englishNumbers, englishFiles);
    var keyArray = [];
    var valueArray = [];
    var resultFiles = {};
    if (checkArray(englishOrders)) {
        for (var key in englishOrders) {
            keyArray.push(key);
            valueArray.push(englishOrders[key]);
        }
        for (var i = 0; i < valueArray.length; i++) {
            var valMin = valueArray[i];
            var keyMin = keyArray[i];
            for (var j = i + 1; j < valueArray.length + 1; j++) {
                if (valueArray[j] < valMin) {
                    var valTemp = valueArray[i];
                    var keyTemp = keyArray[i];
                    valMin = valueArray[j];
                    keyMin = keyArray[j];
                    valueArray[i] = valMin;
                    keyArray[i] = keyMin;
                    valueArray[j] = valTemp;
                    keyArray[j] = keyTemp;
                }
            }
        }

        for (var i = 0; i < keyArray.length; i++) {
            resultFiles[keyArray[i]] = englishFiles[keyArray[i]];
        }

        englishFiles = resultFiles;
    } else {
        for (var key in englishNumbers) {
            keyArray.push(key);
            valueArray.push(englishNumbers[key]);
        }
        for (var i = 0; i < valueArray.length; i++) {
            var valMin = valueArray[i];
            var keyMin = keyArray[i];
            for (var j = i + 1; j < valueArray.length + 1; j++) {
                if (valueArray[j] < valMin) {
                    var valTemp = valueArray[i];
                    var keyTemp = keyArray[i];
                    valMin = valueArray[j];
                    keyMin = keyArray[j];
                    valueArray[i] = valMin;
                    keyArray[i] = keyMin;
                    valueArray[j] = valTemp;
                    keyArray[j] = keyTemp;
                }
            }
        }

        for (var i = 0; i < keyArray.length; i++) {
            resultFiles[keyArray[i]] = englishFiles[keyArray[i]];
        }

        englishFiles = resultFiles;
    }
}

/*function sortingFiles(sortingFiles) {
 var resultArray = [];
 var resultFiles = {};
 for (var key in sortingFiles) {
 resultArray.push(key);
 }

 var length;
 for (index = resultArray.length - 1; index >=0; index--) {
 resultFiles[resultArray[index]] = sortingFiles[resultArray[index]];
 }

 sortingFiles = resultFiles;
 }*/

function sortingFiles(sortingOrders, sortingNumbers, files) {
    if (checkArray(sortingOrders)) {
        sortingEngine(sortingOrders, files);
    } else {
        sortingEngine(sortingNumbers, files);
    }
}

function sortingEngine(sortingType, files) {
    var keyArray = [];
    var valueArray = [];
    var resultFiles = {};
    for (var key in sortingType) {
        keyArray.push(key);
        valueArray.push(sortingType[key]);
    }
    for (var i = 0; i < valueArray.length; i++) {
        var valMin = valueArray[i];
        var keyMin = keyArray[i];
        for (var j = i + 1; j < valueArray.length + 1; j++) {
            if (valueArray[j] < valMin) {
                var valTemp = valueArray[i];
                var keyTemp = keyArray[i];
                valMin = valueArray[j];
                keyMin = keyArray[j];
                valueArray[i] = valMin;
                keyArray[i] = keyMin;
                valueArray[j] = valTemp;
                keyArray[j] = keyTemp;
            }
        }
    }

    for (var i = 0; i < keyArray.length; i++) {
        resultFiles[keyArray[i]] = files[keyArray[i]];
    }

    files = resultFiles;
}

function checkArray(checkingArray) {
    var savedValue = -1;
    var was = false;
    for (var key in checkingArray) {
        if(savedValue == checkingArray[key]) {
            return false;
        }

        if (!was) {
            savedValue = checkingArray[key];
            was = true;
        }
    }

    return true;
}



function addFile(files, minDate, maxDate, language) {
    var ticketId = $('#ticket_id').text();
    var index = 1;

    var currentDate = new Date();

    for(var key in files) {
        var min = minDate[key];
        var max = maxDate[key];

        var checkRange = false;
        var src = $("#effective_changing").attr("src");
        if ((min !== 0 || max !== 0) && !(src.indexOf('not_only') + 1)) {
            checkRange = true;
        }
        var isInRange = false;
        if (checkRange) {
            if (min !== 0 && max !== 0) {
                if (dates.inRange(currentDate, min, max)) {
                    isInRange = true;
                }
            } else if (min != 0 && dates.compare(currentDate, min) === 1) {
                isInRange = true;
            } else if (max != 0 && dates.compare(currentDate, max) === -1) {
                isInRange = true;
            }
        }

        if (!checkRange || isInRange) {
            $("#center_tree").append(
                "<div id='" + language + "_center_image_" + index + "' class='" + key + "'>" +
                "<div style='position: relative; height:100%' class='cellTreeTableItem cellTreeTableTopItem effective'>" +
                "<div onclick='' style='outline:none; position:relative; left:0; width: 100%; height: 100%; white-space: nowrap; overflow: hidden;' class='cellTreeTableItemImageValue cellTreeTableTopItemImageValue'>" +
                "<div style='outline:none; margin-left: 15px; position:relative; right: 0'>" +
                "<div class='visiblePane' style='margin-left: 0px; position:relative; left: 0; right:0; height:auto'>" +
                "<div class='hover_ hover_big' style='position: relative; width: 100%'>" +
                "<img src='" + serverUrl + "/view/" + files[key] + "?ticket=" + ticketId + "&amp;pageNum=100500' style='width: 100%; height: 100%'> " +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>"
            );

            centerImageBind(key, language + "_center_image_" + index);
            index++;
        }
    }
}

function centerImageBind(value, binder) {
    var ticketId = $('#ticket_id').text();
    $(document).one("click", "#" + binder, function (e) {
        e.preventDefault();
        cleanPdfSearcher();

        $("#right_dragger").removeClass("disabled_dragger");
        $(".base-fileTabBar").empty();
        $("#right_viewer").css({"top": ""});
        $("#right_viewer").css({"height": "0px"});
        getDocument(ticketId, value, true); //$("#center_tree").hasClass("compact")
    });

    $(document).one("dblclick", "#" + binder, function (e) {
        e.preventDefault();
        var requirementId = $(this).attr("requirementid");
        var isFiles = $("#center_tree").html() == "";

        if (!isFiles) {
            /*$("#center_tree").empty();
             searchDocuments(ticketId,
             [{"first": "rmrs:parent", "second": requirementId}, {"first" : "lifecycle", "second" : "routes_signed"}],
             [],
             false,
             "",
             false,
             true
             );*/
        }
    });
}

//END THE CENTER PANEL