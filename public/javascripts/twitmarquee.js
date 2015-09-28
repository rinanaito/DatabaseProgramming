window.twttr = window.twttr || {};

var processSummizeInternal = function (B) {
    var J = page.trendDescriptions[page.query];
    if (J) {
        $("#trend_info").hide();
        $("#trend_description span").text(_("%{trend} is a popular topic on Twitter right now.", {
            trend: J[0]
        }));
        $("#trend").text(_("%{trend}", {
            trend: J[0]
        }));
        $("#trend_description p").html(J[1]);
        $("#trend_description").show()
    } else {
        $("#trend_description").hide();
        $("#trend_info").show()
    }(J && J[1].length > 0) ? $(".trenddesc").show() : $(".trenddesc").hide();

};

$.fn.isSearchLink = function (A) {
    return this.each(function () {
        var B = $(this);
        B.click(function (C) {

            $("#trends_list li.active a").removeClass("active")
        })
    })
};


function loadTrendDescriptions() {
    $("#trends a").each(function () {
        var A = $(this);
        var C = A.parent().find("em");
        if (C.length) {
            var B = A.text();
            var D = C.text().replace(new RegExp(B.replace(/([^\w])/gi, "\\$1"), "gi"), "<strong>" + B + "</strong>");
            var E = A.attr("title").length ? A.attr("title") : A.attr("name");page.trendDescriptions[E] = [B, D]
        }
    })
}
$(document).ready(function () {

    page.trendDescriptions = {};
    loadTrendDescriptions();

});

window.twttr.bounds = window.twttr.bounds || {};
$.extend(twttr.bounds, {
    Bounds: function (b, d, c, a) {
        this.x = b;
        this.y = d;
        this.width = c;
        this.height = a
    }
});
$.extend(twttr.bounds.Bounds.prototype, {
    encloses: function (a, b) {
        return a > this.x && a < this.x + this.width && b > this.y && b < this.y + this.height
    },
    toString: function () {
        return "(" + this.x + "," + this.y + ") " + this.width + "x" + this.height
    }
});
(function (a) {
    a.fn.extend({
        bounds: function () {
            var c = this.eq(0);
            var b = c.offset();
            return new twttr.bounds.Bounds(b.left, b.top, c.outerWidth(), c.outerHeight())
        }
    })
})(jQuery); 

(function (A) {
    A.extend(window, {
        TrendTip: {
            parseIntDefault: function (B, D) {
                D = D || 0;
                var C = parseInt(B);
                return isNaN(C) ? D : C
            },
            clearBounds: function () {
                this.data("bounds", [])
            },
            addToBounds: function (B) {
                if (!this.data("bounds")) {
                    this.data("bounds", [])
                }
                this.data("bounds").push(B)
            },
            enclosing: function (B, D) {
                if (!this.data("bounds")) {
                    this.data("bounds", [])
                }
                var C = false;
                A.each(this.data("bounds"), function (F, E) {
                    if (E.encloses(B, D)) {
                        C = true
                    }
                });
                return C
            },
            clearScrollInterval: function () {
                clearInterval(this.data("interval"))
            },
            setScrollInterval: function (B) {
                if (this.data("interval")) {
                    this.clearScrollInterval()
                }
                this.data("interval", setInterval(B, 30))
            },
            duplicateContent: function (B) {
                var C = 0;
                B.children().each(function () {
                    C += A(this).outerWidth(true);
                    B.append(A(this).clone())
                });
                B.css({
                    zoom: 1,
                    width: (2 * C) + "px"
                });
                return C
            },
            initScroller: function () {
                var C = this;
                var E = this.duplicateContent(C);
                var B = TrendTip.parseIntDefault(C.css("left"), 0);
                var D = function () {
                    B = (B % E) - 1;
                    C.css({
                        left: B
                    })
                };
                C.bind("trendEnter", function () {
                    C.clearScrollInterval()
                }).bind("trendLeave", function () {
                    C.setScrollInterval(D)
                }).trigger("trendLeave")
            }
        }
    });
    A.extend(A.fn, {
        trendTip: function () {
            var B = false;
            var C = A(this);
            A.extend(C, TrendTip);
            C.initScroller();
            C.find("li a").each(function () {
                var D = A(this).closest("li");
                var E = {
                    mouseenter: function (G) {
                        var F = A(this);
                        A("#trends .inner").trigger("trendLeave");
                        if (C.oldCapturedTrend) {
                            C.oldCapturedTrend.trigger("trendLeave")
                        }
                        C.oldCapturedTrend = F;
                        C.addToBounds(F.bounds());
                        if (C.enclosing(G.pageX, G.pageY)) {
                            A("#trends .inner").trigger("trendEnter");
                            F.trigger("trendEnter");
                            var H = function (I) {
                                if (!C.enclosing(I.pageX, I.pageY)) {
                                    A("#trends .inner").trigger("trendLeave");
                                    F.trigger("trendLeave")
                                }
                            };
                            A(document).bind("mousemove", H);
                            F.bind("trendLeave", function (I) {
                                C.clearBounds();
                                A(document).unbind("mousemove", H)
                            })
                        }
                    },
                    trendenter: function () {
                        if (!C.hoveringTrend) {
                            var F = A(".trendtip");
                            var K = A(this).offset();
                            var J = Math.round(A(this).outerWidth() / 2);
                            var I = Math.round(F.outerWidth() / 2);
                            var H = D.find("a").text();
                            var G = D.find("em.description").html();
                            F.find(".trendtip-trend").html(H);
                            F.find(".trendtip-trend").attr("href", A(this).attr("href")).attr("name", H);
                            if (A.trim(G) != "") {
                                F.find(".trendtip-why").show();
                                F.find(".trendtip-desc").html(G)
                            } else {
                                F.find(".trendtip-why").hide()
                            }
                            B = setTimeout(function () {
                                clearTimeout(B);
                                D.find("a.search_link").addClass("active");
                                F.css({
                                    top: K.top + 35,
                                    left: K.left + J - I,
                                    position: "absolute",
                                    zIndex: 10000
                                });
                                F.fadeIn("fast", function () {
                                    C.hoveringTrend = true
                                });
                                C.addToBounds(F.bounds())
                            }, 400)
                        }
                    },
                    trendleave: function (G) {
                        clearTimeout(B);
                        A("#trends a.search_link").removeClass("active");
                        if (C.hoveringTrend) {
                            var F = A(".trendtip");
                            F.fadeOut("fast");
                            C.hoveringTrend = false
                        }
                    }
                };
                A(this).mouseenter(E.mouseenter).bind("trendEnter", E.trendenter).bind("trendLeave", E.trendleave)
            });
            return this
        }
    })
})(jQuery);


function FrontPage() {
    return $.extend(this, {
        $trends: $("#trends"),
        $trendTip: $(".trendtip:eq(0)")

    })
}


$.extend(FrontPage.prototype, {
	init: function () {
        this.initTrendHover();
    },
    initTrendHover: function () {
        this.hoveringTrend = false;
        var A = this;
        $("#trends ul").trendTip()
    }
});
(jQuery);