from platform import node
from xml.etree import ElementTree
from sunguard.models import busStopInfo
from urllib.parse import urlencode, unquote, quote_plus
from key import servicekey
from datetime import date
import requests
import decimal

serviceKeyDecoded = unquote(servicekey, 'UTF-8')

#정류장 정보를 데이터베이스에 입력
def getBusStopInfo():
    url = 'http://apis.data.go.kr/6260000/BusanBIMS/busStopList'

    params ={'serviceKey' : serviceKeyDecoded, 'numOfRows':8400, 'pageNo':1}
    response = requests.get(url, params= params)
    rootElement = ElementTree.fromstring(response.text)
    iterElement = rootElement.iter(tag = 'item')
    for element in iterElement:
        data = busStopInfo()
        data.busStopId = element.find('bstopid').text
        data.busStopName = element.find('bstopnm').text
        data.gpsX = decimal.Decimal(element.find('gpsx').text)
        data.gpsY = decimal.Decimal(element.find('gpsy').text)
        data.busStopType = element.find('stoptype').text
        data.save()

def getSolaInfo(latitude, longitude):
    today = date.today()
    locdate =  today.strftime("%Y%m%d")
    url = 'http://apis.data.go.kr/B090041/openapi/service/SrAltudeInfoService/getLCSrAltudeInfo'

    params = {'serviceKey': serviceKeyDecoded, 'locdate': locdate,'latitude':latitude, 'longitude':longitude, 'dnYn':'y'}
    response = requests.get(url, params=params)

    return response

#버스 도착 정보 조회 (정류장ID를 기준)
def getBusArrivalInfo(busStopId):
    print('버스 도착 정보 조회 : ', busStopId)
    comingBuses = []

    url = 'http://apis.data.go.kr/6260000/BusanBIMS/stopArrByBstopid'
    params ={'serviceKey' : serviceKeyDecoded, 'Bstopid': busStopId}
    response = requests.get(url, params= params)

    rootElement = ElementTree.fromstring(response.text)
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
    return comingBuses

def findCityCode():
    url = "http://apis.data.go.kr/1613000/BusRouteInfoInqireService/getCtyCodeList"
    params ={'serviceKey' : serviceKeyDecoded}
    response = requests.get(url, params= params)
    response = response.content.decode('utf-8')
    print(response)
    
def getLineInfo(lineId):
    nodeLine = []
    #response까지 init 함수로 묶기
    url = "http://apis.data.go.kr/6260000/BusanBIMS/busInfoByRouteId"
    params ={'serviceKey' : serviceKeyDecoded, 'lineid': lineId}
    response = requests.get(url, params= params)
    response = response.content.decode('utf-8')
    
    rootElement = ElementTree.fromstring(response)
    iterElement = rootElement.iter(tag = 'item') #item 태그 아래 자식 노드를 순회하기 위해 지정
    for element in iterElement:
        busDict = {}
        busDict['bstopidx'] = element.findtext('bstopidx')
        busDict['stopName'] = element.findtext('bstopnm')
        busDict['nodeid'] = element.findtext('nodeid')
        print("=" * 60)
        print("정류장 순번 : ",busDict['bstopidx'])
        print("정류장 이름 : ",busDict['stopName'])
        print("정류장 ID : ",busDict['nodeid'])
        nodeLine.append(busDict)
    nodeLine = tuple(nodeLine)
    return nodeLine
