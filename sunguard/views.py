from json import dumps
import math
from multiprocessing import context
from xml.etree import ElementTree
from decimal import *
from django.http import Http404, JsonResponse #JsonResponse: HttpResponse의 subclass로, JSON-encoded response를 생성할수 있게 해준다.
from django.shortcuts import render
from django.http import HttpResponse
from django.db.models import Q #Q 객체를 이용한 SQL 질의
from .models import busStopInfo #busStopInfo models
from haversine import haversine
import datetime as dt
import numpy
from .api import * #api.py에서 함수 가져옴

def busStopInfoApi(request):
    getBusStopInfo() #함수 실행 테스트
    return HttpResponse("call getBusStopInfo.")

def showNearStops(request):
    #gpsX = logitude gpsY = latitude
    #haversine 사용
    try:
        #GPS 가져와야 함
        #https ssl 인증서 필요함
        longitude = 129.075639
        latitude = 35.179041

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
    comingBuses = []
    if request.method == 'GET' and request.META.get('HTTP_X_REQUESTED_WITH') == 'XMLHttpRequest': #is_ajax()는 장고 4.x에서 삭제됨
        try:
            busStopId = request.GET['busStop_id'] #선택한 출발 정류장ID
            arrivalBuses = getBusArrivalInfo(busStopId) #도착 예정 버스가 list[dict, ...] 형태로 반환

            rootElement = ElementTree.fromstring(arrivalBuses)
            iterElement = rootElement.iter(tag = 'item')

            for element in iterElement:
                busDict = {}
                busDict['stopName'] = element.find('nodenm').text #현재 정류장 이름
                busDict['busNum'] = element.find('lineno').text # 버스 번호
                busDict['busLine'] = element.find('lineid').text # 노선 ID
                try: 
                    busDict['remainTime'] = element.find('min1').text # 남은 도착 시간(분)
                    busDict['remainStops'] = element.find('station1').text # 남은 정류소 수
                except:
                    busDict['remainTime'] = "운행 종료"
                    busDict['remainStops'] = "운행 종료"
                comingBuses.append(busDict)
            context = {
                'arrivalBuses' : comingBuses,
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
    busLine = []
    if request.method == 'GET' and request.META.get('HTTP_X_REQUESTED_WITH') == 'XMLHttpRequest':
        try:
            departure = request.GET['departure'] # 출발 정류장 ID
            selectedBusLine = request.GET['selectedBusLine'] # 선택한 버스 노선

            print("departure : " + departure + "\nselectedBusLine : " + selectedBusLine)

            buslineText = getLineInfo(selectedBusLine) #노선에 있는 정류장 정보를 불러옴
            #print("buslineText : ", buslineText)

            rootElement = ElementTree.fromstring(buslineText)
            iterElement = rootElement.iter(tag = 'item') #item 태그 아래 자식 노드를 순회하기 위해 지정
            for element in iterElement:
                busDict = {}
                busDict['stopName'] = element.findtext('bstopnm')
                busDict['stopId'] = element.findtext('nodeid')
                busDict['stopOrder'] = element.findtext('bstopidx')
                """
                print("=" * 60)
                print("정류장 순번 : ",busDict['bstopidx'])
                print("정류장 이름 : ",busDict['stopName'])
                print("정류장 ID : ",busDict['nodeid'])
                """
                busLine.append(busDict)
            return HttpResponse(dumps(busLine), content_type = "application/json")
        except:
            return JsonResponse({"status" : "fail", "msg" : "departure or selectedBusLine does not exist"})
    else:
        return JsonResponse({"status":"fail", "msg":"Not a vaild request"})
        
def askSeat(request):
    recommendSeat = ''
    if request.method == 'GET' and request.META.get('HTTP_X_REQUESTED_WITH') == 'XMLHttpRequest':
        departureID = request.GET['departureID']
        destinationID = request.GET['destinationID']
        departureStopInfo = {'latitude' : None ,'longitude' : None}
        destinationStopInfo = {'latitude' : None ,'longitude' : None}

        print("departureID : ", departureID)
        print("destinationID : ", destinationID)

        departure = busStopInfo.objects.get(busStopId = departureID)
        departure.gpsX
        departureStopInfo['latitude'] = departure.gpsY
        departureStopInfo['longitude'] = departure.gpsX

        destination = busStopInfo.objects.get(busStopId = destinationID)
        
        destinationStopInfo['latitude'] = destination.gpsY
        destinationStopInfo['longitude'] = destination.gpsX

        print("출발 좌표 : ", departureStopInfo['latitude'], departureStopInfo['longitude'])
        print("도착 좌표 : ", destinationStopInfo['latitude'], destinationStopInfo['longitude'])

        routeAzimuth = route_azimuth(departureStopInfo['latitude'],  departureStopInfo['longitude'], destinationStopInfo['latitude'], destinationStopInfo['longitude'])
        routeAzimuth  = float(routeAzimuth)
        solarAltitude = getSolaInfo()
        #print(solarAltitude)
        
        time = dt.datetime.now()
        now = time.hour
        now_str = ''
        azimuth_path = './body/items/item/azimuth_'
        if now >= 9 and now < 12:
            now_str = '09'
        elif now >= 12 and now < 15:
            now_str = '12'
        elif now >= 15 and now < 18:
            now_str = '15'
        elif now >= 18 and now < 20:
            now_str = '18'
        else:
            now_str = 'night'
        print('현재 시간은 : ', now_str)
        print('azimush_path : ', azimuth_path + now_str )
        
        if now_str != 'night':
            rootElement = ElementTree.fromstring(solarAltitude)
            azimuth = rootElement.find(azimuth_path + now_str).text
            azimuth = azimuth.split('˚')[:1]
            azimuth = ''.join(azimuth)
            azimuth = float(azimuth)
            print('azimuth : ', azimuth)
            recommendSeat = '준비 중'
            #if condition : #route_azi 와 azimuth를 보고 해가 안 드는 자리를 찾아야 함..
            #360 + (routeAzimuth - 180) ~ routeAzimuth
            # testcase 만들어서 검증해보기
            if routeAzimuth - 180 < 0: #진행방향의 오른쪽 범위를 구함
                f = routeAzimuth
                r = 360 + (routeAzimuth - 180)
                if azimuth > f and azimuth < r: #태양의 방위각이 진행방향의 오른쪽 범위에 포함되면
                    recommendSeat = "오른쪽"  #오른쪽 자리 추천
                else:
                    recommendSeat = "왼쪽"
            elif routeAzimuth - 180 >= 0:
                f =  routeAzimuth
                r = routeAzimuth - 180
                if azimuth > f and azimuth < r:
                    recommendSeat = "오른쪽"
                else:
                    recommendSeat = "왼쪽"
        else:
            recommendSeat = 'Take a seat anywhere'

        context = {
            "departureStopInfo" : departureStopInfo,
            "destinationStopInfo" : destinationStopInfo,
            "routeAzimuth" : routeAzimuth,
            #'azimuth' : azimuth,
            'recommendSeat' : recommendSeat,
        }
        return HttpResponse(dumps(context), content_type = "application/json")

def route_azimuth(start_lat, start_lng, end_lat, end_lng):
    start_lat = math.radians(start_lat)
    start_lng = math.radians(start_lng)
    end_lat = math.radians(end_lat)
    end_lng = math.radians(end_lng)

    y = math.sin(end_lng-start_lng)*math.cos(end_lat)
    x = math.cos(start_lat)*math.sin(end_lat)-math.sin(start_lat)*math.cos(end_lat)*math.cos(end_lng-start_lng)
    z = math.atan2(y, x)
    a = numpy.rad2deg(z)
    if a < 0:
        a = 180+(180+a)
    print("경로상 방위각 : ", a)

    return a

# 메인페이지 html을 렌더함.
# 근처에 있는 출발 정류장 리스트를 만들어서 nearStops에 context에 담아서 렌더
# nearStops를 받는 html에서도 같은 이름 사용해야 함.
def index(request):
    nearStops = showNearStops(request)
    print("stops = ",nearStops)
    try:
        context = {
        'nearStops' : nearStops,
        }
    except nearStops.DoesNotExist:
        raise Http404("nearStops does not exist")
    return render(request, 'sunguard/index.html', context)


