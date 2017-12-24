var rscrHref = window.location.href,
    serverUrl = rscrHref.substring(0, rscrHref.lastIndexOf("/"));
var pdfDoc = null,
    pageNum = 1,
    pageRendering = false,
    pageNumPending = null,
    scale = 0,
    currentScale = 0,
    url = null,
    findField = null,
    countMatches = 0,
    indexes = {},
    offsets = [],
    letterCounter = 1,
    stringCounter = 0,
    query = "",
    currentNumber = 0;

$(function() {
    if (sessionStorage.getItem("mode")) {
        $("#center_tree").removeClass("noncompact");
        $("#center_tree").removeClass("compact");
        $("#center_tree").addClass(sessionStorage.getItem("mode"));
    } else if (!$("#center_tree").hasClass("noncompact") && !$("#center_tree").hasClass("compact")) {
        $("#center_tree").addClass("compact");
        sessionStorage.setItem("mode", "compact");
    }

    //Tree
    $(document).on("click", ".cellTreeItem", function () {
        $(".cellTreeItem").removeClass("cellTreeSelectedItem");
        $(this).addClass("cellTreeSelectedItem");
        var src = $("#effective_changing").attr("src");
        if (!(src.indexOf('not_only') + 1)) {
            $("#effective_changing").attr("src", "images/icons/24/not_only_effective.png")
        }
    });

    $("#root_2").on("click", function () {
        $("#root_2_i").toggle();
        toggleTreeItem(this);
    });

    $("#root_2_1-1").on("click", function () {
        $("#root_2_1-1_i").toggle();
        toggleTreeItem(this);
    });

    function toggleTreeItem(context) {
        var img = $(context).find("img");
        if (img.hasClass("tree-item__image--plus")) {
            img.removeClass("tree-item__image--plus");
            img.addClass("tree_minus_image");
        } else {
            img.removeClass("tree_minus_image");
            img.addClass("tree-item__image--plus");
        }
    }
    //End Tree

    //The view buttons handling
    $("#compact_button").on("click", function () {
        $("#center_tree").empty();
        if ($("#center_tree").hasClass("noncompact")) {
            $("#center_tree").removeClass("noncompact");
            $("#center_tree").addClass("compact");
            sessionStorage.setItem("mode", "compact");
            var reqId = $('#current_requirement_id').text();//$(".cellTreeSelectedItem").attr("requirementid");
            var navSrc = $("#navigate").attr("src");
            if (navSrc.indexOf('up') + 1) {
                centerAddAllItems(reqId);
            } else {
                centerBack(reqId);
            }
        } else {
            $("#center_tree").removeClass("compact");
            $("#center_tree").addClass("noncompact");
            sessionStorage.setItem("mode", "noncompact");
            centerImageAppender();
        }
    });

    $("#lang_changing").on("click", function () {
        if ($("#center_tree").hasClass("noncompact")) {
            $("#center_tree").empty();
            var src = $("#lang_changing").attr("src");
            if (src.indexOf('eng') + 1) {
                $("#lang_changing").attr("src", "images/flags/24/rus.png")
                centerImageAppender();
            } else {
                $("#lang_changing").attr("src", "images/flags/24/eng.png")
                centerImageAppender();
            }
        }
    });

    $("#navigate").on("click", function () {
        $("#center_tree").empty();
        var src = $("#navigate").attr("src");
        var isNested = false;

        if (src.indexOf('up') + 1) {
            isNested = false;
            $("#navigate").attr("src", "images/buttons/24/navigate_down.png");
            if ($("#center_tree").hasClass("noncompact")) {
                centerImageAppender();
            } else {
                var reqId = $('#current_requirement_id').text();//$(".cellTreeSelectedItem").attr("requirementid");
                centerBack(reqId);
            }
        } else {
            isNested = true;
            $("#navigate").attr("src", "images/buttons/24/navigate_up.png");
            if ($("#center_tree").hasClass("noncompact")) {
                centerImageAppender();
            } else {
                var reqId = $('#current_requirement_id').text();//$(".cellTreeSelectedItem").attr("requirementid");
                centerAddAllItems(reqId);
            }
        }
    });

    $("#effective_changing").on("click", function () {
        $("#center_tree").empty();
        var src = $("#effective_changing").attr("src");
        if (src.indexOf('not_only') + 1) {
            $("#effective_changing").attr("src", "images/icons/24/effective.png")
        } else {
            $("#effective_changing").attr("src", "images/icons/24/not_only_effective.png")
        }

        if ($("#center_tree").hasClass("noncompact")) {
            centerImageAppender();
        } else {
            var reqId =  $('#current_requirement_id').text();// $(".cellTreeSelectedItem").attr("requirementid");
            var navSrc = $("#navigate").attr("src");
            if (navSrc.indexOf('up') + 1) {
                centerAddAllItems(reqId);
            } else {
                centerBack(reqId);
            }
        }
    });
    //End of The view button handling

    //Draggers
    var xPrev = 0;
    var clicking = false;
    var left = false;
    $(document).on("mouseup", ".fillContainer", function() {
        clicking = false;
    });
    $(document).on("mousedown", "#left_dragger", function(e) {
        e.preventDefault();
        clicking = true;
        left = true;
    });
    $(document).on("mousedown", "#right_dragger", function(e) {
        e.preventDefault();
        clicking = true;
        left = false;
    });

    $(".fillContainer").on("mousemove", function( event ) {
        if (clicking == false) return;
        var maximumDrugSize = parseInt($("#left_dragger").css('left').replace("px", "")) + parseInt($("#right_dragger").css('right').replace("px", ""));
        var containerMax = $("#center_container").width();
        if (left) {
            var rightDragger = parseInt($("#right_dragger").css('right').replace("px", ""));
            if (rightDragger > containerMax - event.pageX) {
                xPrev = event.pageX - 1;
                if (maximumDrugSize + 16 > containerMax) {
                    $("#left_dragger").css('left', (containerMax - rightDragger - 16) + "px");
                    $("#left_panel").css('width', (containerMax - rightDragger - 16) + "px");
                }
            }
            if (xPrev < event.pageX && maximumDrugSize < (containerMax - 16)) {
                //to the right
                $("#left_panel").css('width', (event.pageX - 8) + "px");
                $("#left_dragger").css('left', (event.pageX - 8) + "px");
                $("#bottom_center_panel").css('left', event.pageX + "px");
                $("#bottom_dragger").css('left', event.pageX + "px");
                $("#center_panel").css('left', event.pageX + "px");
                $("#requirements_search_center_panel").css('left', event.pageX + "px");
            } else if (xPrev > event.pageX) {
                $("#left_panel").css('width', (event.pageX - this.offsetLeft - 8) + "px");
                $("#left_dragger").css('left', (event.pageX - this.offsetLeft - 8) + "px");
                $("#bottom_center_panel").css('left', event.pageX - this.offsetLeft + "px");
                $("#bottom_dragger").css('left', event.pageX - this.offsetLeft + "px");
                $("#center_panel").css('left', event.pageX - this.offsetLeft + "px");
                $("#requirements_search_center_panel").css('left', event.pageX - this.offsetLeft + "px");
            }
        } else {
            var leftDragger = parseInt($("#left_dragger").css('left').replace("px", ""));
            if (leftDragger > event.pageX) {
                xPrev = event.pageX + 1;
                if (maximumDrugSize + 8 > containerMax) {
                    $("#right_dragger").css('right', (containerMax - leftDragger - 16) + "px");
                    $("#right_panel").css('width', (containerMax - leftDragger - 16) + "px");
                }
            }
            if (xPrev < event.pageX && !$("#right_dragger").hasClass("disabled_dragger")) {
                //to the right
                $("#right_panel").css('width', ($("#center_container").width() - event.pageX  - 8) + "px");
                $("#right_dragger").css('right', ($("#center_container").width() - event.pageX - 8) + "px");
                $("#bottom_center_panel").css('right', $("#center_container").width() - event.pageX + "px");
                $("#bottom_dragger").css('right', $("#center_container").width() - event.pageX + "px");
                $("#center_panel").css('right', $("#center_container").width() - event.pageX + "px");
                $("#requirements_search_center_panel").css('right', $("#center_container").width() - event.pageX + "px");
            } else if (xPrev > event.pageX && !$("#right_dragger").hasClass("disabled_dragger") && maximumDrugSize < (containerMax - 16)) {
                $("#right_panel").css('width', ($("#center_container").width() - event.pageX - this.offsetLeft - 8) + "px");
                $("#right_dragger").css('right', ($("#center_container").width() - event.pageX - this.offsetLeft - 8) + "px");
                $("#bottom_center_panel").css('right', $("#center_container").width() - event.pageX - this.offsetLeft + "px");
                $("#bottom_dragger").css('right', $("#center_container").width() - event.pageX - this.offsetLeft + "px");
                $("#center_panel").css('right', $("#center_container").width() - event.pageX - this.offsetLeft + "px");
                $("#requirements_search_center_panel").css('right', $("#center_container").width() - event.pageX - this.offsetLeft + "px");
            }
        }

        xPrev = event.pageX;
    });
    //End Draggers

    //Popup
    //about program popup
    $("td.show_popup").on("click", function () {
        $("." + $(this).attr("rel")).fadeIn(500);
        $("body").append("<div id='overlay' class='gwt-PopupPanelGlass'></div>");
        $("#overlay").show().css({'filter' : 'alpha(opacity=80)'});
        hidePopup();
        return false;
    });
    $("button.close").on("click", function () {
        $(".gwt-PopupPanel").fadeOut(200);
        $("#overlay").remove("#overlay");
        return false;
    });
    //End about program popup

    //reference item
    $("#referenceItem").on("click", function () {
        $("." + $(this).attr("rel")).toggle();
    });

    $("#admin_guide_id").hover(function () {
        $("." + $(this).attr("rel")).addClass("active-popup");
        $(".user_guide").removeClass("active-popup");
    });
    $("#user_guide_id").hover(function () {
        $("." + $(this).attr("rel")).addClass("active-popup");
        $(".admin_guide").removeClass("active-popup");
    });

    //open documents
    $("#manual_base").on("click", function () {
        hidePopup();
        window.open("help/manual_base.pdf", "_blank");
    });
    $("#manual_ord").on("click", function () {
        hidePopup();
        window.open("/help/manual_ord.pdf", "_blank");
    });
    $("#admin_base").on("click", function () {
        hidePopup();
        window.open("/help/admin_base.pdf", "_blank");
    });
    $("#admin_ord").on("click", function () {
        hidePopup();
        window.open("/help/admin_ord.pdf", "_blank");
    });
    //End open documents
    //End reference item

    //search item
    $("#searchItem").on("click", function () {
        $("#center_panel").hide();
        $(".requirements_rel").show();
        $("#right_dragger").addClass("disabled_dragger");
        setSearchWidth();

        $(document).on("click", "#full_search", function () {
            var ticketId = $('#ticket_id').text();
            var text = $("#content_search").val();
            if (text.length === 0) {
                return;
            }
            setSearchWidth();
            setSearchProperties();

            simpleSearch(ticketId,
                [{"first" : "documentTypes", "second" : "rmrs.requirement*"}, {"first" : "lifecycle", "second" : "routes_signed"}],
                [],
                [text + "*"]
            );
        });

        $(document).on("click", "#search_button", function () {
            var ticketId = $('#ticket_id').text();
            var shouldQuery;
            var mustQuery;

            var number = $("#search_number").val();
            //var included = $("#search_included").val();
            //var author = $("#search_author").val();

            var content = $("#search_main_content").val();
            if (content.length === 0 && number.length === 0) {
                return;
            } else if (number.length === 0) {
                mustQuery = [{"first" : "ecm:kind", "second" : "rmrs.requirement"}, {"first" : "lifecycle", "second" : "routes_signed"}];
                shouldQuery = [{"first" : "cm:title", "second" : content + "*"}, {"first" : "rmrs:tags", "second" : content + "*"}];
            } else if (content.length === 0) {
                mustQuery = [{"first" : "ecm:number", "second" : number + "*"} , {"first" : "ecm:kind", "second" : "rmrs.requirement"}, {"first" : "lifecycle", "second" : "routes_signed"}];
                shouldQuery = [];
            } else {
                mustQuery = [{"first" : "ecm:number", "second" : number + "*"} , {"first" : "ecm:kind", "second" : "rmrs.requirement"}, {"first" : "lifecycle", "second" : "routes_signed"}];
                shouldQuery = [{"first" : "cm:title", "second" : content + "*"}, {"first" : "rmrs:tags", "second" : content + "*"}];
            }
            setSearchWidth();
            setSearchProperties();
            $("#right_dragger").addClass("disabled_dragger");

            simpleSearch(ticketId,
                mustQuery,
                shouldQuery,
                []
            );
        });
    });

    function setSearchProperties() {
        $("#search_center_tree").empty();
        $("#search_center_tree").addClass("loading");
        $("#search_button").prop("disabled", true);
        $(".cancel_from_search").prop("disabled", true);
        $(".new_search").prop("disabled", true);
        $("#full_search").prop("disabled", true);
    }

    $(".advanced_search").on("click", function () {
        $("#center_panel").hide();
        $(".requirements_rel").hide();
        $("." + $(this).attr("rel")).show();
        setSearchWidth();
    });

    $("#requirements_id").on("click", function () {
        $("#center_panel").hide();
        $(".advanced").hide();
        $("." + $(this).attr("rel")).show();
        setSearchWidth();
    });

    $(".cancel_from_search").on("click", function () {
        $(".requirements_rel").hide();
        $(".advanced").hide();
        $("#search_center_tree").empty();
        $("#center_panel").show();
        $("#right_dragger").removeClass("disabled_dragger");
        setRightPanel();
    });

    function setSearchWidth() {
        $("#right_panel").css('width', "8px");
        $("#right_dragger").css('right', "0px");
        $("#bottom_center_panel").css('right', "0px");
        $("#bottom_dragger").css('right', "8px");
        $("#requirements_search_center_panel").css('right', "8px");
        $("#search_right_close").addClass("base-ImageButton-disabled");
        $("#search_right_close").removeClass("base-ImageButton");
    }

    $(".new_search").on("click", function () {
        $("#search_center_tree").empty();
        $(".search_items input").val("");
        $("#right_dragger").addClass("disabled_dragger");
        setSearchWidth();
    });

    $("#content_clean").on("click", function () {
        $("#content_search").val("");
    });
    //End search item

    function hidePopup() {
        $(".referencePopup").fadeOut(100);
        $(".searchPopup").fadeOut(100);
        $(".user_guide").removeClass("active-popup");
        $(".admin_guide").removeClass("active-popup");
    }

    $("#main-container").on("mouseup", function () {
        hidePopup();
    });

    $(".gwt-MenuBar-vertical tr").hover(function (e) {
        $(this).find("td.gwt-MenuItem").toggleClass("gwt-MenuItem-selected", e.type === 'mouseenter');
        $(this).find("td.subMenuIcon").toggleClass("subMenuIcon-selected", e.type === 'mouseenter');
    });
    $(".gwt-MenuBar-horizontal tr td.gwt-MenuItem").hover(function (e) {
        $(this).toggleClass("gwt-MenuItem-selected", e.type === 'mouseenter');
    });
    //End Popup

    //PDF VIEWER
    /**
     * Zoom in a page.
     */
    $("#plus_image").on("click", function () {
        $("#svg_rendering").html('');
        if (scale == 0.25) {
            scale = scale + 0.05;
        } else {
            scale = Math.round((scale + 0.1)*10)/10;
        }

        renderPage(pageNum, scale);
    });

    /**
     * Zoom out a page.
     */
    $("#minus_image").on("click", function () {
        if (scale != 0.25) {
            $("#svg_rendering").html('');
            if (scale == 0.3) {
                scale = 0.25;
            } else {
                scale = Math.round((scale - 0.1)*10)/10;
            }
            renderPage(pageNum, scale);
        }
    });

    /**
     * Displays previous page.
     */
    $("#previousPage").on("click", function () {
        if (pageNum <= 1) {
            return;
        }
        pageNum--;
        renderPage(pageNum, scale);
    });

    /**
     * Displays next page.
     */
    $("#nextPage").on("click", function () {
        if (pageNum >= pdfDoc.numPages) {
            return;
        }

        pageNum++;
        renderPage(pageNum, scale);
    });

    window.addEventListener("keydown",function (e) {
        if (e.keyCode === 114 || (e.ctrlKey && e.keyCode === 70)) {
            // Block CTRL + F event
            e.preventDefault();
            $("#browser_search").trigger('click');
            pdfSearchPreparation();
        }
    });

    $("#browser_search").on("click", function () {
        if ($("#findbar").hasClass("hidden")) {
            $("#findbar").removeClass("hidden");
        } else {
            $("#findbar").addClass("hidden");
        }
        $("#findResultsCount").addClass("hidden");
        //$("#findbar").toggleClass("hidden");
        //$("#findbar").fadeIn(100);
        pdfSearchPreparation();
    });
    $("#fullscreen_mode").on("click", function () {
        var element = document.getElementById("svg_rendering");
        currentScale = scale;
        renderPage(pageNum, 1.2);
        launchFullScreen(element);
        element.addEventListener("webkitfullscreenchange", onfullscreenchange);
    });
    //END PDF VIEWER
});

////////////////////ADDITIONAL FUNCTIONS/////////////////////

//SEARCHING

function pdfSearchPreparation() {
    findField.select();
    findField.focus();
    findField.on('input', function() {
        pdfSearch();
    });
    findField.on('keydown', function(evt) {
        switch (evt.keyCode) {
            case 13: // Enter
                pdfSearch();
                break;
            case 27: // Escape
                cleanPdfSearcher();
                break;
        }
    });
}

function searchCircle(text, query, index, indexes, offsets) {
    var foundNumber = text.search(query);
    currentNumber += foundNumber;
    if (text.length == 0 || foundNumber == -1 || query === "") {
        return 0;
    }

    //indexes.push(index);
    offsets.push(currentNumber);
    text = text.substr(foundNumber + query.length, text.length)
    searchCircle(text, query, index, indexes, offsets);
}

function pdfSearch() {
    var notFound = false;

    query = findField.val();
    var fullTextElements = $(".textLayer");
    var pieces = fullTextElements.find("div");
    var fullText = "";
    var currentIndex = -1;
    var currentPieceNumber = -1;
    var currentPiece = "";
    var leftPiece = "";
    var rightPiece = "";
    var highlightText = "";
    var matchCase = true;

    indexes.length = 0;
    indexes = {};
    offsets.length = 0;
    offsets = [];
    fullText = "";
    pieces.each(
        function (index) {
            var piece = $(this).text();
            searchCircle(piece, query, index, indexes, offsets);
            currentNumber = 0;
            fullText += piece;
        }
    );

    if(!$("#findMatchCase").prop("checked"))  {
        matchCase = false;
        query = query.toUpperCase();
        fullText = fullText.toUpperCase();
    }

    pieces.each(
        function (index) {
            var piece = $(this).text();
            if (!matchCase) {
                piece = piece.toUpperCase();
            }
            indexes[index] = piece;
            var pieceNumber = piece.search(query);
            if (pieceNumber >= 0) {
                currentIndex = index;
                currentPieceNumber = pieceNumber;
                currentPiece = piece;
                return false;
            }

        }
    );

    highlightText = currentPiece.substr(currentPieceNumber, query.length);
    leftPiece = currentPiece.substr(0, currentPieceNumber)
    rightPiece = currentPiece.substr(currentPieceNumber + query.length, currentPiece.length)

    if (currentPieceNumber >= 0) {
        pieces.each(
            function (index) {
                if (currentIndex == index) {
                    $(this).html(leftPiece + "<div class='highlight selected in_block'>" + highlightText + "</div>" + rightPiece);
                    //$(this).addClass("highlight");
                }
            }
        );
    } else {
        notFound = true;
    }

    /*    if (indexes.length >= 0) {
     pieces.each(
     function (index) {
     var index, len;
     for (ind = 0, len = indexes.length; ind < len; ++ind) {
     if (indexes[ind] == index) {
     currentPieceNumber = offsets[ind];
     highlightText = currentPiece.substr(currentPieceNumber, query.length);
     leftPiece = currentPiece.substr(0, currentPieceNumber)
     rightPiece = currentPiece.substr(currentPieceNumber + query.length, currentPiece.length)
     $(this).html(leftPiece + "<div class='highlight selected in_block'>" + highlightText + "</div>" + rightPiece);
     }
     }
     }
     );
     } else {
     notFound = true;
     }*/

    countMatches = 0;
    findAllMatches(fullText, query.trim());
    $("#findResultsCount").text(countMatches);
    $("#findResultsCount").removeClass("hidden");

    if (notFound) {
        findField.addClass('notFound');
    } else {
        this.findField.removeClass('notFound');
    }

    $("#findPrevious").on("click", function () {
        if (offsets.length >= 0 && letterCounter > 0) {
            var currentPieceNumber = offsets[letterCounter];
            if (typeof currentPieceNumber == 'undefined') {
                stringCounter--;
            }
            var currentPiece = indexes[stringCounter];
            if (typeof currentPiece == 'undefined') {
                return false;
            }
            var highlightText = currentPiece.substr(currentPieceNumber, query.length);
            var leftPiece = currentPiece.substr(0, currentPieceNumber)
            var rightPiece = currentPiece.substr(currentPieceNumber + query.length, currentPiece.length)
            pieces.each(
                function (index) {
                    if (stringCounter == index) {
                        $(this).html(leftPiece + "<div class='highlight selected in_block'>" + highlightText + "</div>" + rightPiece);

                    }
                }
            );
            if(letterCounter > 1) {
                letterCounter--;
            }
        }
    });
    $("#findNext").on("click", function () {
        if (offsets.length >= 0 && letterCounter > 0) {
            var currentPieceNumber = offsets[letterCounter];
            if (typeof currentPieceNumber == 'undefined') {
                stringCounter++;
            }
            var currentPiece = indexes[stringCounter];
            if (typeof currentPiece == 'undefined') {
                return false;
            }
            var highlightText = currentPiece.substr(currentPieceNumber, query.length);
            var leftPiece = currentPiece.substr(0, currentPieceNumber)
            var rightPiece = currentPiece.substr(currentPieceNumber + query.length, currentPiece.length)
            pieces.each(
                function (index) {
                    if (stringCounter == index) {
                        $(this).html(leftPiece + "<div class='highlight selected in_block'>" + highlightText + "</div>" + rightPiece);

                    }
                }
            );
            letterCounter++;
        }
    });
}

function findAllMatches(text, query) {
    var foundNumber = text.search(query);
    if (text.length == 0 || foundNumber == -1 || query === "") {
        return 0;
    }

    countMatches++;
    text = text.substr(foundNumber + query.length, text.length)
    findAllMatches(text, query);
}

//END SEARCHING


//FULLSCREEN MODE
function launchFullScreen(element) {
    if(element.requestFullScreen) {
        element.requestFullScreen();
    } else if(element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
    } else if(element.webkitRequestFullScreen) {
        element.webkitRequestFullScreen();
    }
}

var onfullscreenchange =  function(e){
    if ($("#svg_rendering").hasClass("presentation_mode")) {
        $("#svg_rendering").removeClass("presentation_mode");
        renderPage(pageNum, currentScale);
    } else {
        $("#svg_rendering").addClass("presentation_mode");
    }
}
//END FULLSCREEN MODE


//HEADER PANEL. AUTHORIZATION LINK
function addAuthorization(serverUrl) {
    $("#authorization").append(
        "<td align='left' style='vertical-align: top;'>" +
        "<a class='gwt-Anchor' href='" + serverUrl + "' style='text-decoration: none;'>Авторизоваться</a>" +
        "</td>"
    );
}
//END HEADER PANEL. AUTHORIZATION LINK

//LEFT AND RIGHT ARROWS
function closeButtonBind() {
    $(document).on("click", ".left_close_button", function (e) {
        e.preventDefault();
        if ($("#left_panel").css('width') !== "0px") {
            $("#left_panel").css('width', "0px");
            $("#left_dragger").css('left', "0px");
            $("#bottom_center_panel").css('left', "8px");
            $("#bottom_dragger").css('left', "8px");
            $("#center_panel").css('left', "8px");
            $("#advanced_search_center_panel").css('left', "8px");
            $("#requirements_search_center_panel").css('left', "8px");
            $(".closeButtonLeft").css('background-image', "url('images/buttons/32/right.png')");
        } else {
            $("#left_panel").css('width', "387px");
            $("#left_dragger").css('left', "387px");
            $("#bottom_center_panel").css('left', "395px");
            $("#bottom_dragger").css('left', "395px");
            $("#center_panel").css('left', "395px");
            $("#advanced_search_center_panel").css('left', "395px");
            $("#requirements_search_center_panel").css('left', "395px");
            $(".closeButtonLeft").css('background-image', "url('images/buttons/32/left.png')");
        }
    });

    $(document).on("click", ".right_close_button", function (e) {
        e.preventDefault();
        if ($("#right_panel").css('width') !== "0px" && $(this).find(".base-ImageButton-disabled").length == 0) {
            $("#right_panel").css('width', "0px");
            $("#right_dragger").css('right', "0px");
            $("#bottom_center_panel").css('right', "8px");
            $("#bottom_dragger").css('right', "8px");
            $("#center_panel").css('right', "8px");
            $("#requirements_search_center_panel").css('right', "8px");
            $(".closeButtonRight").css('background-image', "url('images/buttons/32/left.png')");
        } else if($(this).find(".base-ImageButton-disabled").length === 0) {
            $("#right_panel").css('width', "387px");
            $("#right_dragger").css('right', "387px");
            $("#bottom_center_panel").css('right', "395px");
            $("#bottom_dragger").css('right', "395px");
            $("#center_panel").css('right', "395px");
            $("#requirements_search_center_panel").css('right', "395px");
            $(".closeButtonRight").css('background-image', "url('images/buttons/32/right.png')");
        }
    });
}

function setRightPanel() {
    $("#right_panel").css('width', "387px");
    $("#right_dragger").css('right', "387px");
    $("#bottom_center_panel").css('right', "395px");
    $("#bottom_dragger").css('right', "395px");
    $("#center_panel").css('right', "395px");
    $("#requirements_search_center_panel").css('right', "8px");
}

function setSearchRightPanel() {
    $("#right_panel").css('width', "387px");
    $("#right_dragger").css('right', "387px");
    $("#bottom_center_panel").css('right', "395px");
    $("#bottom_dragger").css('right', "395px");
    $("#requirements_search_center_panel").css('right', "395px");
    $(".closeButtonRight").css('background-image', "url('images/buttons/32/right.png')");

    $("#search_right_close").removeClass("base-ImageButton-disabled");
    $("#search_right_close").addClass("base-ImageButton");
}

function backSearchRightPanel() {
    $("#right_panel").css('width', "387px");
    $("#right_dragger").css('right', "387px");
    $("#bottom_center_panel").css('right', "395px");
    $("#bottom_dragger").css('right', "395px");
    $("#requirements_search_center_panel").hide();
}
//END LEFT AND RIGHT ARROWS

//TREE
function toggleTreeItem(context, count) {
    var img = $(context).find("img");
    var span = $(context).find("span");
    if (img.hasClass("tree-item__image--plus")) {
        img.removeClass("tree-item__image--plus");
        img.removeClass("i_" + (count - 1));
        img.addClass("tree_minus_image");
        img.attr("src", "images/minus.png");
        img.addClass("i_" + count);

        span.removeClass("tree-item__folder--closed");
        span.addClass("tree-item__folder--opened");
    } else {
        img.removeClass("tree_minus_image");
        img.removeClass("i_" + (count - 1));
        img.addClass("tree-item__image--plus");
        img.attr("src", "images/plus.png");
        img.addClass("i_" + count);

        span.removeClass("tree-item__folder--opened");
        span.addClass("tree-item__folder--closed");
    }
}
//END TREE

function getDateFns() {
    var dateFunctions = {
        convert: function (d) {
            return (
                d.constructor === Date ? d :
                    d.constructor === Array ? new Date(d[0], d[1], d[2]) :
                        d.constructor === Number ? new Date(d) :
                            d.constructor === String ? new Date(d) :
                                typeof d === "object" ? new Date(d.year, d.month, d.date) :
                                    NaN
            );
        },
        compare: function (a, b) {
            return (
                isFinite(a = this.convert(a).valueOf()) &&
                isFinite(b = this.convert(b).valueOf()) ?
                    (a > b) - (a < b) :
                    NaN
            );
        },
        inRange: function (d, start, end) {
            return (
                isFinite(d = this.convert(d).valueOf()) &&
                isFinite(start = this.convert(start).valueOf()) &&
                isFinite(end = this.convert(end).valueOf()) ?
                    start <= d && d <= end :
                    NaN
            );
        }
    }

    return dateFunctions;
}

//PDF VIEWER
function addImageCanvas(ticketId, imageId) {
    var url = serverUrl + "/view/" + imageId +"?ticket=" + ticketId + "&asPdf";
    PDFJS.workerSrc = 'pdfviewer/pdf.worker.js';
    var loadingTask = PDFJS.getDocument(url);
    loadingTask.promise.then(function(pdf) {
        console.log('PDF loaded');
        // Fetch the first page
        var pageNumber = 1;
        pdf.getPage(pageNumber).then(function(page) {
            console.log('Page loaded');

            var scale = 0.9;
            var viewport = page.getViewport(scale);

            // Prepare canvas using PDF page dimensions
            var canvas = document.getElementById('the-canvas');
            var context = canvas.getContext('2d');
            canvas.height = viewport.height;
            canvas.width = viewport.width;

            // Render PDF page into canvas context
            var renderContext = {
                canvasContext: context,
                viewport: viewport
            };
            var renderTask = page.render(renderContext);
            renderTask.then(function () {
                console.log('Page rendered');
            });
        });
    }, function (reason) {
        // PDF loading error
        console.error(reason);
    });


    $("#viewer").append(
        "<div id='pageContainer1' class='page' data-page-number='1' style='width: 327px;height: 462px;' data-loaded='true'>" +
        "<div class='canvasWrapper' style='width: 327px; height: 462px;'>" +
        "<canvas id='the-canvas' style='width: 327px; height: 463px;'></canvas>" +
        "</div>" +

        "<div class='textLayer' style='width: 327px; height: 462px; opacity: 1;'>" +
        /*"<canvas id='the-canvas' style='width:365px; height:205px;'></canvas>" +*/
        /*"<iframe id='frameid' src='" + serverUrl + "/view/" + imageId +"?ticket=" + ticketId + "&asPdf' style='width:365px; height:205px;' frameborder='0' ></iframe>" +*/
        "</div>" +
        "</div>" +
        "</div>"
    );
}

function addImage(ticketId, imageId) {
    var url = serverUrl + "/view/" + imageId +"?ticket=" + ticketId + "&asPdf";
    PDFJS.workerSrc = 'pdfviewer/pdf.worker.js';
    var loadingTask = PDFJS.getDocument(url);

    loadingTask.promise.then(function(pdf) {
        console.log('PDF loaded');
        pdfDoc = pdf;
        scale = 0.5;
        renderPage(pageNum, scale);
    }, function (reason) {
        // PDF loading error
        console.error(reason);
    });

    $("#viewer").append(
        "<div id='pageContainer1' class='page' data-page-number='1' style='width: 327px;height: 462px;' data-loaded='true'>" +
        "<div class='canvasWrapper' style='width: 327px; height: 462px;'>" +
        "<div id='svg_rendering' style='width: 327px; height: 463px;'></div>" +
        /*"<canvas id='the-canvas' style='width: 327px; height: 463px;'></canvas>" +*/
        "</div>" +

        "<div class='textLayer' style='width: 327px; height: 462px; opacity: 1;'>" +
        /*"<canvas id='the-canvas' style='width:365px; height:205px;'></canvas>" +*/
        /*"<iframe id='frameid' src='" + serverUrl + "/view/" + imageId +"?ticket=" + ticketId + "&asPdf' style='width:365px; height:205px;' frameborder='0' ></iframe>" +*/
        "</div>" +
        "</div>" +
        "</div>"
    );
}

function renderPage(num, customScale) {
    var percent = (customScale*100).toFixed(0);
    $("#size_in_percent").text(percent + "%");
    $("#outOfPages").text("/" + pdfDoc.numPages);
    $("#numberOfPages").val(num);
    if (pdfDoc.numPages == 1) {
        $("#numberOfPages").attr("disabled", true);
        $("#previousPage > div").removeClass("base-ImageButton");
        $("#previousPage > div").addClass("base-ImageButton-disabled");
        $("#nextPage > div").removeClass("base-ImageButton");
        $("#nextPage > div").addClass("base-ImageButton-disabled");
    } else if (num == 1) {
        $("#previousPage > div").removeClass("base-ImageButton");
        $("#previousPage > div").addClass("base-ImageButton-disabled");
        $("#nextPage > div").removeClass("base-ImageButton-disabled");
        $("#nextPage > div").addClass("base-ImageButton");
    }  else if (pdfDoc.numPages == num) {
        $("#previousPage > div").removeClass("base-ImageButton-disabled");
        $("#previousPage > div").addClass("base-ImageButton");
        $("#nextPage > div").removeClass("base-ImageButton");
        $("#nextPage > div").addClass("base-ImageButton-disabled");
    } else {
        $("#previousPage > div").removeClass("base-ImageButton-disabled");
        $("#previousPage > div").addClass("base-ImageButton");
        $("#nextPage > div").removeClass("base-ImageButton-disabled");
        $("#nextPage > div").addClass("base-ImageButton");
    }

    pageRendering = true;
    // Using promise to fetch the page
    var container = document.getElementById("svg_rendering");
    //for (var i = 1; i <= pdfDoc.numPages; i++) {

    // Get desired page
    pdfDoc.getPage(num).then(function(page) {
        $("#svg_rendering").html('');
        var viewport = page.getViewport(customScale);
        // Set dimensions
        $("#svg_rendering").css("width", viewport.width + 'px');
        $("#svg_rendering").css("height", viewport.height + 'px');
        $("#pageContainer1").css("width", viewport.width + 'px');
        $("#pageContainer1").css("height", viewport.height + 'px');
        $(".canvasWrapper").css("width", viewport.width + 'px');
        $(".canvasWrapper").css("height", viewport.height + 'px');
        $(".textLayer").css("width", viewport.width + 'px');
        $(".textLayer").css("height", viewport.height + 'px');


        var div = document.createElement("div");

        // Set id attribute with page-#{pdf_page_number} format
        div.setAttribute("id", "page-" + (page.pageIndex + 1));

        // This will keep positions of child elements as per our needs
        div.setAttribute("style", "position: relative");

        // Append div within div#container
        container.appendChild(div);

        // Create a new Canvas element
        var canvas = document.createElement("canvas");

        // Append Canvas within div#page-#{pdf_page_number}
        div.appendChild(canvas);

        var context = canvas.getContext('2d');
        canvas.height = viewport.height;
        canvas.width = viewport.width;

        var renderContext = {
            canvasContext: context,
            viewport: viewport
        };

        // Render PDF page
        page.render(renderContext)
            .then(function() {
                // Get text-fragments
                return page.getTextContent();
            })
            .then(function(textContent) {
                // Create div which will hold text-fragments
                var textLayerDiv = document.createElement("div");

                // Set it's class to textLayer which have required CSS styles
                textLayerDiv.setAttribute("class", "textLayer");

                // Append newly created div in `div#page-#{pdf_page_number}`
                div.appendChild(textLayerDiv);

                // Create new instance of TextLayerBuilder class
                var textLayer = new TextLayerBuilder({
                    textLayerDiv: textLayerDiv,
                    pageIndex: page.pageIndex,
                    viewport: viewport
                });

                // Set text-fragments
                textLayer.setTextContent(textContent);

                // Render text-fragments
                textLayer.render();
            });

        findField = $("#findInput");
    });

    /*        pdfDoc.getPage(num).then(function (page) {
     console.log('Page loaded');
     var viewport = page.getViewport(customScale);

     // Set dimensions
     $("#svg_rendering").css("width", viewport.width + 'px');
     $("#svg_rendering").css("height", viewport.height + 'px');
     $("#pageContainer1").css("width", viewport.width + 'px');
     $("#pageContainer1").css("height", viewport.height + 'px');
     $(".canvasWrapper").css("width", viewport.width + 'px');
     $(".canvasWrapper").css("height", viewport.height + 'px');
     $(".textLayer").css("width", viewport.width + 'px');
     $(".textLayer").css("height", viewport.height + 'px');

     // SVG rendering by PDF.js
     page.getOperatorList()
     .then(function (opList) {
     var svgGfx = new PDFJS.SVGGraphics(page.commonObjs, page.objs);
     return svgGfx.getSVG(opList, viewport);
     })
     .then(function (svg) {
     $("#svg_rendering").html('');
     $("#svg_rendering").append(svg);
     });

     findField = $("#findInput");
     });*/
    //}

    // Update page counters
    //$("#numberOfPages").val(num);
    //document.getElementById('page_num').textContent = pageNum;
}

/**
 * If another page rendering in progress, waits until the rendering is
 * finised. Otherwise, executes rendering immediately.
 */
function queueRenderPage(num, customScale) {
    if (pageRendering) {
        pageNumPending = num;
    } else {
        renderPage(num, customScale);
    }
}

//END PDF VIEWER

function addCenterTitle(value) {
    $("#right_close_button_id").after(
        "<div id='center_title' style='position: absolute; overflow: hidden; left: 32px; top: 5px; right: 32px; height: 40px;'>" +
        "<div class='desktop-FolderName zero-position--absolute'>" + value.name + "</div>" +
        "</div>"
    );
}