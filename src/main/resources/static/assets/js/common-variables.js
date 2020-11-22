let commonVariablesJs = (function () {

    let variables = new Map();
    let optionTitle = "<option value=\"\" selected disabled hidden> -- select variable -- </option>";
    let apiQueryVariableRegEx = new RegExp('\\$\\w+', 'g');

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
                const responseData = this.callApiQuery(variable.apiQuery);
                this.renderVariableData(variable, isGenerated, responseData);
            } else {
                let apiQuery = variable.apiQuery;
                extractVariables.forEach(edgeField => {
                    if ($("#var-" + edgeField).val() === null) {
                        this.setGenerateVariableData(this.getVariable(edgeField), isGenerated);
                    }
                    apiQuery = apiQuery.replace(new RegExp('\\$' + edgeField, 'g'), $("#var-" + edgeField).val())
                });

                const responseData = this.callApiQuery(apiQuery);
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
            switch (variableType) {
                case "METRIC_LABEL_VALUES":
                    const values = responseData.data.result.map(data => data.metric[variable.name]).sort();
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
        callApiQuery: function (apiQuery) {
            return this.ajaxRequest(apiQuery);
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
        getRequest: function (uri) {
            return axios.get(apiHost + uri.replace(/\+/g, "%2B"));
        },
        ajaxRequest: function (uri) {
            let response = null;
            $.ajax({
                url: apiHost + uri.replace(/\+/g, "%2B"),
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
