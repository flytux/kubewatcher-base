let commonVariablesJs = (function () {

    let variables = new Map();
    let optionTitle = "<option value=\"\" selected disabled hidden> -- select variable -- </option>";
    // let apiQueryVariableRegEx = new RegExp('\\$\\w+', 'g');
    let apiQueryVariableRegEx = new RegExp('\\$[^0-9]\\w+', 'g');

    /*공개 멤버*/
    return {
        generateVariable: function () {
            for (const [key, value] of variables) {
                this.setGenerateVariableData(value, true);
            }
        },
        setGenerateVariableData: function (variable, isGenerated) {
            if (variable === undefined) {
                return;
            }
            const extractVariables = this.extractVariables(variable.apiQuery);
            if (extractVariables === null) {
                const responseData = this.callApiQuery(variable.queryType, variable.apiQuery);
                this.renderVariableData(variable, isGenerated, responseData);
            } else {
                let apiQuery = variable.apiQuery;
                extractVariables.forEach(edgeField => {
                    if ($("#var-" + edgeField).val() === null) {
                        this.setGenerateVariableData(this.getVariable(edgeField), isGenerated);
                    }
                    apiQuery = apiQuery.replace(new RegExp('\\$' + edgeField, 'g'), $("#var-" + edgeField).val())
                });

                const responseData = this.callApiQuery(variable.queryType, apiQuery);
                this.renderVariableData(variable, isGenerated, responseData);
            }
        },
        refreshEdgeVariableData: function (selectedId) {
            const variable = this.getVariable(selectedId);
            variable.edgeFields.forEach(edgeField => this.setGenerateVariableData(this.getVariable(edgeField), false));
        },
        renderVariableData: function (variable, isGenerated, responseData) {
            let $selector = $("#var-" + variable.name);
            const variableType = variable.variableType;
            let renderHtml = "";
            let values = undefined;
            switch (variableType) {
                case "METRIC_LABEL_VALUES":
                    values = responseData.data.result.map(data => data.metric[variable.name]).sort();
                    for (let value of values) {
                        if (value !== undefined) {
                            renderHtml += "<option value=\"" + value + "\">" + value + "</option>";
                        }
                    }
                    break;
                case "API_LABEL_VALUES":
                    values = responseData.map(data => data[variable.name]);
                    for (let value of values) {
                        if (value !== undefined) {
                            renderHtml += "<option value=\"" + value + "\">" + value + "</option>";
                        }
                    }
                    break;
                default:
                    console.warn("unsupported variable type")
            }
            $selector.empty();
            $selector.append(optionTitle + renderHtml);
            if (isGenerated) {
                $("#var-" + variable.name + " option:eq(1)").prop("selected", true);
            }
            if (renderHtml === "") {
                $selector.attr("disabled", true);
            } else {
                $selector.attr("disabled", false);
            }
        },
        callApiQuery: function (queryType, apiQuery) {
            if (queryType === "METRIC") {
                return this.ajaxRequest(apiHost + encodeURI(apiQuery).replace(/\+/g, "%2B"));
            } else if (queryType === "PROXY_METRIC") {
                return this.ajaxRequest("/proxy/prometheus" + encodeURI(apiQuery).replace(/\+/g, "%2B"));
            }
            return this.ajaxRequest(encodeURI(apiQuery).replace(/\+/g, "%2B"));
        },
        addVariable: function (variable) {
            variables.set(variable.name, variable);
        },
        getVariable: function (name) {
            return variables.get(name);
        },
        createVariables: function (object) {
            if (object.length) {
                object.forEach(variable => this.addVariable(variable));
            }
        },
        getRequest: function (url) {
            return axios.get(url);
        },
        ajaxRequest: function (url) {
            let response = null;
            $.ajax({
                url: url,
                type: "GET",
                async: false,
                success: function (json) {
                    response = json;
                },
                error: function (e) {
                    console.error(e);
                }
            })
            return response;
        },

        getSelectedValueByVariable: function (variableName) {
            $("#var-" + variableName).val();
        },
        convertVariableApiQuery: function (apiQuery) {
            const extractVariables = this.extractVariables(apiQuery);
            if (extractVariables === null) {
                return apiQuery;
            }
            extractVariables.forEach(edgeField => {
                apiQuery = apiQuery.replace(new RegExp('\\$' + edgeField, 'g'), $("#var-" + edgeField).val())
            });
            return apiQuery;
        },
        extractVariables: function (apiQueryUri) {
            const matches = apiQueryUri.match(apiQueryVariableRegEx);
            return matches !== null ? matches.map(item => item.replace("$", "")) : matches;
        },
        nothingPromiseFunc: function () {
            return new Promise(() => console.log("return nothing"));
        },
        isSelectedAllVariable: function () {
            const readyVariable = $.makeArray($("select[name^='var-']")
                .map(function (item) {
                    return $(this).val() != null
                }));
            return !readyVariable.includes(false);
        }
    };
}());
