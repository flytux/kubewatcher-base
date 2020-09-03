let variablesJs = (function () {

    function variableValueAppend () {
        let valueAppend = "";
        $("select[name^='var-']").map(function () {
            if (valueAppend !== "") {
                valueAppend += " - "
            }
            valueAppend += $(this).val();
        });
        return valueAppend;
    }

    /*공개 멤버*/
    return {
        setEdgeFieldValue: function (sourceField, targetField) {
            let $selector = $("#var-" + targetField),
                sourceFieldValue = $("#var-" + sourceField).val(),
                targetTemplateVariable = templating.get(targetField),
                refIds = targetTemplateVariable.refIds,
                query = targetTemplateVariable.src;
            if (!refIds.length) {
                query = query.replace(new RegExp("\\" + "$" + sourceField, "g"), sourceFieldValue);
            } else {
                for (const refId of refIds) {
                    if (sourceField === refId) {
                        query = query.replace(new RegExp("\\" + "$" + sourceField, "g"), sourceFieldValue);
                    } else {
                        if ($("#var-" + refId).val() === null) {
                            $selector.attr("disabled", true);
                            $selector.html(optionTitle);
                            return;
                        } else {
                            query = query.replace(new RegExp("\\" + "$" + refId, "g"), $("#var-" + refId).val());
                        }
                    }
                }
            }
            this.renderVariable(sourceField, sourceFieldValue, $selector, query);

        }
        , renderVariable: function (sourceField, sourceFieldValue, $targetSelector, targetQuery) {
            // 데이터 호출

            let targetValue = this.ajaxFunction("/proxy/api", targetQuery);
            if (!targetValue.length) {
                return;
            }

            // option 랜더링
            let optionsHtml = optionTitle;
            for (const value of targetValue) {
                optionsHtml += "<option value=\"" + value + "\">" + value + "</option>";
            }
            $targetSelector.empty();
            $targetSelector.append(optionsHtml);
            $targetSelector.attr("disabled", false);
        }
        , ajaxFunction: function (url, query) {
            let response = null;
            url = url + "?query=" + encodeURIComponent(query);
            $.ajax({
                url: url,
                type: "GET",
                async: false,
                success: function (json, aJaxtatus) {
                    response = json;
                },
                error: function (e) {
                    alert("error");
                }
            })
            return response;
        }
        , reloadPanel: function (target) {
            $(document).find(target !== undefined ? target : 'iframe').map(function () {
                var varRegexp = new RegExp("(var-\\w+=[\\w.:-]+)", "g")
                $this = $(this),
                    panelSrc = $this.attr('src'),
                    title = "";

                let matchArr = panelSrc.match(varRegexp);
                for (const element of matchArr) {
                    let keyValue = element.split("=");
                    let key = keyValue[0];

                    let newValue = $("#" + key).val();
                    if (newValue === undefined) {
                        continue;
                    }

                    if (newValue === null) {
                        return;
                    }

                    title = title + "-" + newValue;
                    panelSrc = panelSrc.replace(new RegExp(element, "g"), key + "=" + newValue);

                }
                $this.attr('src', panelSrc);
                console.log(panelSrc);
                $('#namespace').text(variableValueAppend());
                $('#namespace').attr('class', 'd-inline');
            });
        }

    };
}());
