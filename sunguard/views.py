from json import dumps
from unicodedata import decimal
from decimal import *
from django.http import Http404, JsonResponse #JsonResponse: HttpResponse의 subclass로, JSON-encoded response를 생성할수 있게 해준다.
from django.shortcuts import render
from django.http import HttpResponse
from django.db.models import Q #Q 객체를 이용한 SQL 질의
from .models import busStopInfo #busStopInfo models
from haversine import haversine
from .api import getBusStopInfo, getBusArrivalInfo, getLineInfo #api.py에서 함수 가져옴

def busStopInfoApi(request):
    getBusStopInfo() #함수 실행 테스트
    return HttpResponse("call getBusStopInfo.")

def showNearStops():
    #gpsX = logitude gpsY = latitude
    #haversine 사용

    try:
        #GPS 가져와야 함
        #https ssl 인증서 필요함
        longitude = 129.129570420443
        latitude = 35.192761930615

        position = (latitude, longitude)
        condition = (
            Q(gpsX__range = (longitude - 0.005, longitude + 0.005)) | Q(gpsY__range = (latitude - 0.0025, latitude + 0.0025))
        )
        busStop_Info = (
            busStopInfo.objects.filter(condition)
        )
        #busStop_Info = busStop_Info[0:10]
        #print("busStop_Info : \n",busStop_Info)
        nearBusStopInfo = [info for info in busStop_Info if haversine(position, (info.gpsY, info.gpsX)) <= 0.3] #범위
        nearBusStopInfo = tuple(nearBusStopInfo)
        #print("nearBusStopInfo : \n",nearBusStopInfo)
        #print("\n\ninfo[0] : ", nearBusStopInfo[0].busStopName)
    except:
        print('erorr')
    return nearBusStopInfo

#사용자가 선택한 버스 정류장의 ID를 넘겨받아 해당 버스 정류장의 정보(도착 버스, 남은 시간, 정류장 이름)를 조회
def busArrivalInfo(request):
    if request.method == 'GET' and request.META.get('HTTP_X_REQUESTED_WITH') == 'XMLHttpRequest': #is_ajax()는 장고 4.x에서 삭제됨
        try:
            busStopId = request.GET['busStop_id'] #선택한 출발 정류장ID
            arrivalBuses = getBusArrivalInfo(busStopId) #도착 예정 버스가 list[dict, ...] 형태로 반환
            print("\narrivalBuses : ", arrivalBuses[0])
            context = {
                'arrivalBuses' : arrivalBuses[0],
            }
            return HttpResponse(dumps(context), content_type = "application/json")
        except arrivalBuses.DoesNotExist:
            return JsonResponse({"status" : "fail", "msg" : "arrivalBuses does not exist"})
    #print("도착 예정 버스 \n\n",arrivalBuses)
    else:
        return JsonResponse({"status":"fail", "msg":"Not a vaild request"})

def getBusStopPosition(StopId):
    stop = busStopInfo.objects.filter(busStopId=StopId)
    return stop


#각 버스가 갖고있는 노선번호를 주고 노선 순서를 가져오는 함수
#정류장 순서, 정류장 이름, ...
def retrieveLineInfo(request):
    if request.method == 'GET':
        lineId = request.GET['busStop_id']
        line = getLineInfo(lineId) #노선에 있는 정류장 정보를 불러옴
        for stop in line:
            stopId = getBusStopPosition() # line에 있는 정류장에서 정류장id를 넘겨줌
    return line

# 메인페이지 html을 렌더함.
# 근처에 있는 출발 정류장 리스트를 만들어서 nearStops에 context에 담아서 렌더
# nearStops를 받는 html에서도 같은 이름 사용해야 함.
def index(request):
    nearStops = showNearStops()
    print("stops = ",nearStops)
    try:
        context = {
        'nearStops' : nearStops,
        }
    except nearStops.DoesNotExist:
        raise Http404("nearStops does not exist")
    return render(request, 'sunguard/index.html', context)


