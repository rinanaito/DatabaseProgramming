/**
 * Created by .
 * User: batbold
 * Date: 5/11/11
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
jQuery.fn.forceNumericOnly =
    function (decimal) {
        $(this).keydown(function (e) {
            var key = e.charCode || e.keyCode || 0;
            // allow backspace, tab, delete, arrows, numbers and keypad numbers ONLY
            return (
                key == 8 ||
//                                    key==190 ||  // is point
                key == 9 ||
                key == 46 ||
                (key >= 37 && key <= 40) ||
                (key >= 48 && key <= 57) ||
                (key >= 96 && key <= 105));
        });
        $(this).keyup(function (e) {
            if (decimal && $(this).val().length > 0) {
                var value = $(this).val(), c = 1, vv = "";
                for (var i = 0; i < value.length; i++) {
                    if (value.charAt(i) != "'")vv += value.charAt(i);
                }
                value = vv;
                vv = "";
                for (i = value.length - 1; i >= 0; i--) {
                    if (i != 0 && c == 3) {
                        vv += value.charAt(i) + "'";
                        c = 1
                    } else {
                        vv += value.charAt(i);
                        c++;
                    }
                }
                value = "";
                for (i = vv.length - 1; i >= 0; i--) {
                    value += vv.charAt(i);
                }
                $(this).val(value);
            }
        });
    };

jQuery.fn.forceNumericOnlyLength =
    function (len) {
        $(this).keydown(function (e) {
            var key = e.charCode || e.keyCode || 0;
            if (key != 8 &&
                key != 9 &&
                key != 46 &&
                !(key >= 37 && key <= 40)
                && $(this).val().length >= len)return false;
            // allow backspace, tab, delete, arrows, numbers and keypad numbers ONLY
            var check = (
                key == 8 ||
//                                    key==190 ||  // is point
                key == 9 ||
                key == 46 ||
                (key >= 37 && key <= 40) ||
                (key >= 48 && key <= 57) ||
                (key >= 96 && key <= 105));
        });
    };

