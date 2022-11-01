

// 쿠키를 저장하는 함수
function setCookie(cookie_name, value, days){
    var exdate = new Date();
    exdate.setDate(exdate.getDate() + days);
    //  설정 일수만큼 현재시간에 만료값으로 지정
    var cookie_value = escape(value) + ((days == null) ? '' : '; expires=' + exdate.toUTCString());
    document.cookie = cookie_name + '=' + cookie_value;
}
// 쿠키를 불러오는 함수
function getCookie(cookie_name) {
    var x, y;
    var val = document.cookie.split(';');

    for (var i = 0; i < val.length; i++) {
        x = val[i].substr(0, val[i].indexOf('='));
        y = val[i].substr(val[i].indexOf('=') + 1);
        x = x.replace(/^\s+|\s+$/g, ''); // 앞과 뒤의 공백 제거하기
        if (x == cookie_name) {
        return unescape(y); // unescape로 디코딩 후 값 리턴
        }
    }
}           

$(document).ready(function(){
    $("#departure_btn").click(function(){
        let departure = $("select[name=departureStop]").val(); //select option 값 
        console.log("departure option : ", departure);
        
        setCookie("departure",departure, 1); //출발 정류장 쿠키에 저장 -> 출발지/목적지 설정에 사용

        $.ajax({
            url: 'arrivalbuses/',
            type : 'GET',
            data : {
                'busStop_id' : departure,
            },
            dataType : 'json',
            async : false,
            success : function(response){ //응답을 기다리고 있다가 data 넘어오면 동작
                //선택한 버스를 넘겨주고, 버스도착 정보를 받아서 테이블로 뿌려줌
                if(response != undefined && response != ''){
                    var data = response.arrivalBuses;
                    //alert(JSON.stringify(data.arrivalBuses[0].busNum));
                    var str = '<TR>';
                    $.each(data , function(i){
                        str += '<TD>' + data[i].busNum + '</TD><TD>' + data[i].busLine + '</TD><TD>' + data[i].remainTime + '</TD><TD>' + data[i].remainStops + '</TD>';
                        str += '</TR>';
                    });
                    $("#arrival_buses_table_body").html(str);
                };
            }
        });
    });
});