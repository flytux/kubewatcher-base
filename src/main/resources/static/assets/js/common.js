let comnJs = (function(){
    /*비공개 멤버*/

    /*
    * ajax 통신 기본값 object
    * - 추가로 필요한 값이 있을 경우 추가 가능
    * */
    let aobj = {
        url: "",
        type: "POST",
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        done: null,
        fail: null,
        always: null
    };

    /*공개 멤버*/
    return {
        /*
        * <문자열 빈값 체크>
        * 빈값일 때 : true
        * 빈값이 아닐 때 : false
        * */
        isEmpty: function(str) {
            if(typeof str === "undefined" || str === null || str === "")    return true;
            else                                                            return false;
        }
        /*
        * <문자열 빈값 체크 : 기본 문자열 리턴>
        * 문자열이 빈값일 경우 기본 문자열을 리턴한다.
        * - str : 빈값 체크할 문자열
        * - dStr : 빈값일 때 리턴할 기본 문자열(default)
        * ex)
        * comnJs.nvl('abc', '123') return 'abc'
        * comnJs.nvl('', '123')    return '123'
        * */
        , nvl: function(str, dStr) {
            return !this.isEmpty(str) ? str : dStr;
        }
        /*
        * <문자열 변환>
        * - str : 원본 문자열
        * - tStr : 변환할 문자열(target)
        * - nStr : 변환될 문자열(new)
        * ex)
        * comnJs.replace('test-abc', 'test-', '')    return 'abc'
        * */
        , replace: function(str, tStr, nStr) {
            return !this.isEmpty(str) ? str.replace(tStr, nStr) : false;
        }
        /*
        * <Ajax 통신>
        * data 객체에 담은 정보로 ajax 통신한다.
        * 필요하지 않은 값은 세팅하지 않아도 되도록 비공개 멤버 aobj를 이용해 default value 지정.
        * 추가로 필요한 데이터가 있을 경우 aobj에 key:value 추가하여 사용 가능.
        * - data : ajax 통신에 필요한 값을 담은 object
        * ex)
        * let data = {
        *   url: "http://test.com",
        *   type: "get",
        *   ...
        * }
        * comnJs.ajax(data)
        * */
        , ajax: function(data) {
            if(!data.url)
                return alert("URL이 입력되지 않았습니다.");

            if(typeof data === 'object') {
                for(let key in aobj)
                    aobj[key] = !this.isEmpty(data[key]) ? aobj[key] : data[key];
            }

            return new Promise(function(resolve, reject){
                $.ajax({
                    url: aobj.url,
                    type: aobj.type,
                    dataType: aobj.dataType,
                    contentType: aobj.contentType
                }).done(function(data){
                    if(typeof aobj.done === 'function')
                        aobj.done(data);
                }).fail(function(err){
                    if(typeof aobj.fail === 'function')
                        aobj.fail();
                }).always(function(){
                    if(typeof aobj.always === 'function')
                        aobj.always();
                });
            }).then(function(resolve){
                //console.log(resolve);
                console.log('then');
            }).catch(function(reject){
                //console.log(reject);
                console.log('catch');
            });
        }
        /*
        * <Paging>
        *
        * */
        , pagenation: function(page){
            let html = '',
                pageSize = 10,      //한 화면에 보여줄 데이터 row = 10개
                pageBlock = 10,     //한 번에 보여줄 nav 갯수 = 10개 ( < 1 | 2 | 3 ... | 10 >)
                totalCount = 201,
                paramPage = Number(location.search.replace('?pageNo=', '')),
                pageNum = paramPage == 0 ? 1 : paramPage;

            let totalPage = Math.ceil(totalCount / pageSize);
            if(totalPage < pageNum) pageNum = totalPage;

            let ePage = Math.ceil(pageNum / pageBlock) * pageBlock;
            if(ePage > totalPage)   ePage = totalPage;

            let sPage = ePage - pageBlock + 1;

            if(sPage < pageBlock) {
                html += '<li><a href="#" aria-label="Previous"><span aria-hidden="ture" class="feather icon-chevron-left"/></a></li> \n';
            } else {
                html += '<li><a href="' + location.pathname + '?pageNo=' + (pageNum - pageBlock) + '" aria-label="Previous"><span aria-hidden="ture" class="feather icon-chevron-left"/></a></li> \n';
            }

            for(let i = sPage; i <= ePage; i ++){
                if(i == pageNum) {
                    html += '<li class="active"><a href="#">' + i + '</a></li> \n';
                } else {
                    html += '<li><a href="/cluster/network/endpoints?pageNo=' + i + '">' + i + '</a></li> \n';
                }
            }

            if(ePage < totalPage) {
                let nextPage = pageNum + pageBlock > totalPage ? totalPage : pageNum + pageBlock;
                html += '<li><a href="' + location.pathname + '?pageNo=' + nextPage + '" aria-label="Next"><span aria-hidden="ture" class="feather icon-chevron-right"/></a></li> \n';
            } else {
                html += '<li><a href="#" aria-label="Next"><span aria-hidden="ture" class="feather icon-chevron-right"/></a></li> \n';
            }

            $('.pagination li').remove();
            $('.pagination').append(html);
        }
    };
}());
