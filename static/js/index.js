

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

// 테이블의 Row 클릭시 노선정보 쿠키에 저장
$(document).on("click","#arrival_buses_table_body > tr", function(){ 	
    
    var str = ""
    var tdArr = new Array();	// 배열 선언
    // 현재 클릭된 Row(<tr>)
    var tr = $(this);
    var td = tr.children(); //각 열에 입력된 데이터
    console.log(td);
    
    // tr.text()는 클릭된 Row 즉 tr에 있는 모든 값을 가져온다.
    console.log("클릭한 Row의 모든 데이터 : "+tr.text());
    
    // 반복문을 이용해서 배열에 값을 담아 사용할 수 도 있다.
    td.each(function(i){
        tdArr.push(td.eq(i).text());
    });
    
    console.log("배열에 담긴 값 : "+tdArr);
    
    // td.eq(index)를 통해 값을 가져올 수도 있다.
    var busNum = td.eq(0).text();
    var busLine = td.eq(1).text();
    var remainTime = td.eq(2).text();
    var remainStops = td.eq(3).text();
    
    
    str +=	" 버스 번호 : " + busNum +
            ", 노선 번호 : " + busLine +
            ", 남은 시간 : " + remainTime +
            ", 남은 정류장 : " + remainStops;		
    console.log(str);
    
    setCookie("selectedBusLine", busLine, 1); //버스노선 쿠키에 추가

    var departure = getCookie("departure");
    var selectedBusLine = getCookie("selectedBusLine");

    console.log("출발 정류장 : " + departure + "\n 선택 버스라인 : " + selectedBusLine);

    //노선정보를 서버로 보내 버스의 경로 정보 받아오기
    $.ajax({
        url: 'retrieveLineInfo/',
        type: 'GET',
        data : {
            'departure' : departure,
            'selectedBusLine' : selectedBusLine
        },
        dataType : 'json',
        async : false,
        success : function(busLineInfo){
            if(busLineInfo != undefined){
                //alert(JSON.stringify(busLineInfo));
                //서버로부터 받아온 노선의 순서를 도착 정류장 <select>에 입력
                $("#destination_select").empty(); //기존 옵션 제거
                
                $.each(busLineInfo , function(i){
                        var option = $("<option value = " + busLineInfo[i].stopId + ">" + busLineInfo[i].stopName+"</option>");
                        $("#destination_select").append(option);
                });
                
            };
        }
    });
});

//도착 버튼 클릭시 좌석 추천 
$(document).on("click", "#destination_bnt", function(){
    var departure = getCookie("departure", 1);
    var selectedDestination = $("#destination_select option:selected").val(); //선택된 옵션값

    console.log("selectedDestination : " + selectedDestination);
    var departureID = getCookie('departure',1)

    $.ajax({
        url : 'askSeat/',
        type : 'GET',
        data : {
            'departureID' : departureID,
            'destinationID' : selectedDestination,
        },
        dataType : 'json',
        success : function(recommendSeat){
            alert(JSON.stringify(recommendSeat));
        }
    });
});

//출발 정류장 서버에 알리고 서버로부터 해당 정류장의 도착 정보 받아오기
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
                    var str = '<tr>';
                    $.each(data , function(i){
                        str += '<td>' + data[i].busNum + '</td><td>' + data[i].busLine + '</td><td>' + data[i].remainTime + '</td><td>' + data[i].remainStops + '</td>';
                        str += '</tr>';
                    });
                    $("#arrival_buses_table_body").html(str);
                };
            }
        });

        //console.log("선택 정류장 : " + getCookie("departure"));
    });
});

