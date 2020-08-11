let pTempJs = (function(){

    /*
    * 페이지 로딩할 때 menu_id로 ajax 날려서 리턴 값 받은 다음 그 후의 처리로 예상됨.
    *
    * 화면 특성상 예외적인 element 등을 처리하기 위해 panelTemplate에서 append하지 않고
    * html 형태로 호출한 화면에 return 해줄 것
    * */

    let type, title, itemId, dataType, item, src,
        fullWidth = 12,
        divide = 0,
        isModal = false;

    const fgcolorArray = ['a4df5d', '37d8ae', '50bee3', '5da2e5', '7392ed', 'be7fc6'],
          colorArray = ['164, 223, 93', '55, 216, 174', '80, 190, 227', '93, 162, 229', '115, 146, 237', '190, 127, 198'];

    let makeTableHtml = function(itemList) {
        let headArray = itemList.head,
            bodyMap = itemList.body,
            headHtml = '',
            bodyHtml = '',
            idx = 0,
            subIdx = 1;

        for(let i in bodyMap) {
            bodyHtml += '<tr>';

            for(let j in headArray) {
                let headItem = headArray[j],
                    bodyItem = bodyMap[i][headItem],
                    seq = bodyMap[i][headArray[0]];

                if(i == 0 && j > 0){
                    if(headItem === 'Level')    headHtml += '<th style="padding-right:25px;">' + headItem + '</th>';
                    else                        headHtml += '<th>' + headItem + '</th>';
                }

                if(j > 0 && !comnJs.isEmpty(bodyItem)) {
                    if(isModal) {
                        bodyHtml += '<td data-toggle="modal" data-target="#myModal' + seq + '" onclick="pTempJs.openModal(' + seq + ')">' + bodyItem + '</td>';
                    } else {
                        if(headItem === 'Closed') {
                            bodyHtml += '<td>';
                            bodyHtml += '   <div class="radio_new figure">';
                            bodyHtml += '      <input type="radio" name="ex_rds' + idx + '" id="ex_rd' + idx + '" ' + (bodyItem === 'Y' ? 'checked="true"' : '') + '>';
                            bodyHtml += '      <label for="ex_rd' + idx + '"> Yes</label>&nbsp;&nbsp;';
                            bodyHtml += '      <input type="radio" name="ex_rds' + idx + '" id="ex_rd' + subIdx + '" ' + (bodyItem === 'N' ? 'checked="true"' : '') + '>';
                            bodyHtml += '      <label for="ex_rd' + subIdx + '"> No</label>';
                            bodyHtml += '   </div>';
                            bodyHtml += '</td>';

                            idx = idx + 2, subIdx = subIdx + 2;
                        } else if(headItem === 'Level') {
                            bodyHtml += '<td><div class="circle c_' + bodyItem + '"></div></td>';
                        } else {
                            bodyHtml += '<td>' + bodyItem + '</td>';
                        }
                    }
                }
            }

            bodyHtml += '</tr>';
        }

        return [headHtml, bodyHtml];
    }, makeChartBoxHtml = function(key, value) {
        let html = '';

        if(type === 'updown-card')  html += '<div class="row col-xs-6">';
        else                        html += '<div class="col-xs-12 col-md-12 col-lg-' + divide + ' makeTag" id="' + itemId + '">';

        html += '   <div class="col-xs-12 box_chart">';
        html += '       <div class="title_chart_c">' + key + '</div>';
        html += '       <div class="t_chart text-center">';

        if(value.match(/%/gi))      html += comnJs.replace(value, '%', '') + '<span class="t_sstxt2">%</span>';
        else                        html += value;

        html += '       </div>';
        html += '   </div>';
        html += '</div>';

        return html;
    };

    let card = function(panels, idx) {
        let html = '';

        if(type === 'card') {
            html += '<div class="col-xs-12 col-md-12 col-lg-' + divide + ' makeTag" id="' + itemId + '">';
            html += '   <div class="col-xs-12 box1" id="' + itemId + '">';
            html += '       <div class="title">' + title + '</div>';
            html += '       <div class="b_txt text-center">' + item + '</div>';
            html += '   </div>';
            html += '</div>';
        } else if(type === 'card-badge') {
            let fgcolor = '007219', bgcolor = '7cb78a', color = '0, 114, 25', state = 'Normal';

            item = Number(item);

            if(50 < item && item <= 80){
                fgcolor = 'e08e00';
                bgcolor = 'f9d390';
                color = '224, 142, 0';
                state = 'Warning';
            } else if(item > 80) {
                fgcolor = 'cd2b30';
                bgcolor = 'e88071';
                color = '205, 43, 48';
                state = 'Danger';
            }

            html += '<div class="col-md-12 col-lg-' + divide + ' makeTag" id="' + itemId + '">';
            html += '   <div class="col-md-12 box_' + state.toLowerCase() + '">';
            html += '       <div class="row">';
            html += '           <div class="col-xs-12">';
            html += '               <div class="title">' + title + '</div>';
            html += '           </div>';
            html += '       </div>';
            html += '       <div class="row">';
            html += '           <div class="col-xs-5">';
            html += '               <div class="b_txt">' + state + '<span class="b_stxt"></span></div>';
            html += '           </div>';
            html += '           <div class="col-xs-7 text-right" style="vertical-align: bottom;">';
            html += '               <input class="knob" data-width="90" data-height="90" data-angleoffset="0" data-thickness=".2" data-fgcolor="#' + fgcolor + '" data-bgcolor="#' + bgcolor + '" value="' + item + '" ';
            html += 'style="width: 49px; height: 30px; position: absolute; vertical-align: middle; margin-top: 30px; margin-left: -69px; border: 0px; background: none; ';
            html += 'font: bold 18px Arial; text-align: center; color: rgb(' + color + '); padding: 0px; appearance: none;">';
            html += '           </div>';
            html += '       </div>';
            html += '   </div>';
            html += '</div>';
        } else if(type === 'updown-card') {
            let upDown = panels.closed ? 'DOWN' : 'UP';
            let setClass = {'UP': 'box1_pb15', 'DOWN': 'box1_down'};

            html += '<div class="col-xs-12 col-md-12 col-lg-4 makeTag" id="' + itemId + '">';
            html += '   <div class="col-xs-12 ' + setClass[upDown] + '">';
            html += '       <div class="row col-xs-6">';
            html += '           <div class="title col-xs-12">RAC Node#' + (idx + 1) + '</div>';
            html += '       </div>';
            html += '       <div class="row col-xs-6">';
            html += '           <div class="col-xs-12 box_' + upDown.toLowerCase() + '">' + upDown + '</div>';
            html += '       </div>';

            for(let i = 0; i < title.length; i ++)
                html += makeChartBoxHtml(title[i], item[i]);

            html += '   </div>';
            html += '</div>';
        } else if(type === 'card-round') {
            let itemCount = item[0],
                roundValue = item[1],
                itemMap = item[2];
            title = title + itemCount;

            html += '<div class="col-xs-12 col-md-4 col-lg-' + divide + ' makeTag">';
            html += '   <div class="col-xs-12 box_panel">';
            html += '       <div class="box_panel-heading3">' + title + '</div>';
            html += '       <div class="box_panel-text">';
            html += '           <div style="display:inline;width:100px;height:100px;">';
            html += '               <input class="knob" data-linecap="round" data-width="100" data-height="100" data-angleoffset="0" data-thickness=".35" ';
            html += 'data-fgcolor="#' + fgcolorArray[idx] + '" value="' + roundValue + '" style="width: 54px; height: 33px; position: absolute; vertical-align: ';
            html += 'middle; margin-top: 33px; margin-left: -77px; border: 0px; background: none; font: bold 20px Arial; text-align: center; color: rgb(' + colorArray[idx] + '); ';
            html += 'padding: 0px; appearance: none;">';
            html += '           </div>';
            html += '       </div>';
            html += '       <div class="box_panel-stext ml_20">';

            for(let key in itemMap)
                html += '           <div class="circle_s"/>' + key + ' ' + itemMap[key] + '<br>';

            html += '       </div>';
            html += '   </div>';
            html += '</div>';
        } else if(type === 'card-multi-round') {
            html += '<div class="col-xs-6 makeTag" id="' + itemId + '">';
            html += '   <div class="row">';
            html += '       <div class="box_panel-text text-center">' + title + '</div>';
            html += '   </div>';
            html += '   <div class="row">';
            html += '       <div class="box_panel-text">';
            html += '           <div style="display:inline;width:115px;height:116px;">';
            html += '               <input class="knob" data-linecap="round" data-width="115" data-height="116" data-angleoffset="0" ';
            html += 'data-thickness=".4" data-fgcolor="#' + fgcolorArray[idx] + '" value="' + item[0] + '" style="width: 61px; height: 38px; position: absolute; vertical-align: middle; ';
            html += 'margin-top: 38px; margin-left: -88px; border: 0px; background: none; font: bold 23px Arial; text-align: center; color: rgb(' + colorArray[idx] + '); padding: 0px; appearance: none;">';
            html += '           </div>';
            html += '       </div>';
            html += '   </div>';
            html += '   <div class="row">';
            html += '       <div class="box_panel-stext"><div class="circle c_oe" style="background-color: rgb(' + colorArray[idx] + ');"></div>' + item[1];
            html += '           <span class="c_oe_t" style="color: rgb(' + colorArray[idx] + ');">' + item[2] + '</span>/' + item[3];
            html += '       </div>';
            html += '   </div>';
            html += '</div>';
        }

        return html;
    }, box = function(panels, idx) {
        let html = '';

        if(type === 'box') {
            html += '<div class="col-xs-12 col-md-12 col-lg-' + divide + ' makeTag" id="' + itemId + '">';
            html += '    <div class="box_panel">';
            html += '        <div class="box_panel-heading">' + title + '</div>';
            html += '        <div class="box_panel-body">';
            html += '            <img src="/assets/img/' + item + '" class="img-responsive" alt="" style="width:100%;">';
            html += '        </div>';
            html += '    </div>';
            html += '</div>';
        } else if(type === 'box-blank') {
            html += '<div class="box_panel col-xs-12 makeTag" id="' + itemId + '">';
            html += '   <div class="box_panel-body">' + item + '</div>';
            html += '</div>';
        }
        //console.log(type, html);
        return html;
    }, table = function(panels, idx) {
        let html = '',
            tableHtml = makeTableHtml(panels.item);

        if(type === 'table-col') {
            html += '<div class="col-xs-12 col-md-12 col-lg-' + divide + ' makeTag" id="' + itemId + '">';
            html += '    <div class="box_panel">';

            if(!comnJs.isEmpty(title))
                html += '        <div class="box_panel-heading">' + title + '</div>';

            html += '        <div class="box_table-body">';
            html += '            <table class="list_system">';
            html += '                <thead>';
            html += '                    <tr>';
            html +=                          tableHtml[0];
            html += '                    </tr>';
            html += '                </thead>';
            html += '                <tbody>';
            html +=                          tableHtml[1];
            html += '                </tbody>';
            html += '            </table>';
            html += '        </div>';
            html += '    </div>';
            html += '</div>';
        } else if(type === 'table-level') {
            html += '<div class="box_panel makeTag" id="' + itemId + '">';
            html += '   <div class="box_panel-heading2">';
            html += '       <div class="circle c_normal"/>Normal&nbsp;&nbsp;&nbsp;&nbsp;';
            html += '       <div class="circle c_warning"/>Warning&nbsp;&nbsp;&nbsp;&nbsp;';
            html += '       <div class="circle c_danger"/>Danger';
            html += '   </div>';
            html += '   <div class="box_table-body">';
            html += '      <table class="list_system">';
            html += '         <thead>';
            html += '            <tr>';
            html +=                  tableHtml[0];
            html += '            </tr>';
            html += '         </thead>';
            html += '         <tbody>';
            html +=              tableHtml[1];
            html += '         </tbody>';
            html += '      </table>';
            html += '   </div>';
            html += '</div>';
        }
        //console.log(type, title, html);
        return html;
    }, chart = function(panels, idx) {
        let html = '';

        if(type === 'back-chart') {
            html += makeChartBoxHtml(title, item);
        }
        //console.log(html);
        return html;
    };

    return {
        /*
        * <패널별 html 생성>
        * 데이터에 명시된 패널 종류에 따라 html을 생성한다.
        * */
        generatePanelList: function(datas) {
            let returnArray = [];

            for(let data of datas) {
                let html = '';
                divide = fullWidth/data.panels.length;

                for(let i = 0, panels = data.panels; i < panels.length; i ++){
                    type = panels[i].panel_type,
                    title = panels[i].title,
                    itemId = panels[i].panel_id,
                    item = panels[i].item,
                    isModal = panels[i].modal;
                    //console.log(panels[i]);
                    if(type.match(/card/gi))        html += card(panels[i], i);
                    else if(type.match(/table/gi))  html += table(panels[i], i);
                    else if(type.match(/box/gi))    html += box(panels[i], i);
                    else                            html += chart(panels[i], i);
                }

                let returnMap = {rowId:data.row_id, title:data.title, panels:html};
                returnArray.push(returnMap);
            }
            return returnArray;
        },
        openModal: function(id) {
            $('.modal').attr('id', 'myModal' + id);
        },
        /*
        * <예외처리 : class 변경>
        * 특정 태그의 class를 변경한다.
        * - obj : 타겟 태그
        * - i : 변경할 class name
        * - j : 변경될 class name
        * ex)
        * pTempJs.resize($(this), 'test', 'test2');
        * */
        changeClass: function(obj, cls, nCls) {
            obj.removeClass(cls).addClass(nCls);
        },
        /*
        * <예외처리 : 사이즈 변경>
        * 특정 태그의 사이즈를 제어하는 class를 변경한다.
        * - obj : 타겟 태그
        * - i : 변경할 사이즈 class name
        * - j : 변경될 사이즈 class name
        * ex)
        * pTempJs.resize($(this), 4, 6);
        * */
        resize: function(obj, i, j) {
            this.changeClass(obj, 'col-lg-' + i, 'col-lg-' + j);
        },
        /*sizeDivider:function(obj) {
            let objChildLength = obj.children().length,
                makeTagLength = obj.find('.makeTag').length,
                objWidth = obj.width(),
                makeTagLength = obj.find('.makeTag').length,
                tagSize = (objWidth / makeTagLength) / objWidth * 10;   //col-lg-(tagSize)
                //notMakeTagArray = obj.children().not('.makeTag');
            console.log(tagSize);
        },*/
        clone: function(obj) {
            let $elem = obj.data( "arr", [ 1 ] ),
                $clone = $elem.clone( true ).data( "arr", $.extend( [], $elem.data( "arr" ) ) );
            return $clone;
        }
    }
}());