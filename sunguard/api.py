from platform import node
from xml.etree import ElementTree
from sunguard.models import busStopInfo
from urllib.parse import urlencode, unquote, quote_plus
from key import servicekey
from datetime import date
import requests
import decimal



#정류장 정보를 데이터베이스에 입력
def getBusStopInfo():
    url = 'http://apis.data.go.kr/6260000/BusanBIMS/busStopList'

    params ={'serviceKey' : servicekey, 'numOfRows':8400, 'pageNo':1}
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

    params = {'serviceKey': servicekey, 'locdate': locdate,'latitude':latitude, 'longitude':longitude, 'dnYn':'y'}
    response = requests.get(url, params=params)

    return response

#버스 도착 정보 조회 (정류장ID를 기준)
def getBusArrivalInfo(busStopId):
    #comingBuses = []
    print("정류장 ID : ", busStopId)
    url = "http://apis.data.go.kr/6260000/BusanBIMS/stopArrByBstopid?"
    params ={'bstopid': busStopId, "serviceKey" : servicekey}
    response = requests.get(url, params)
    response = response.content.decode('utf-8')

    return response

def findCityCode():
    url = "http://apis.data.go.kr/1613000/BusRouteInfoInqireService/getCtyCodeList"
    params ={'serviceKey' : servicekey}
    response = requests.get(url, params= params)
    response = response.content.decode('utf-8')
    print(response)
    
def getLineInfo(lineId):
    nodeLine = []
    #response까지 init 함수로 묶기
    url = "http://apis.data.go.kr/6260000/BusanBIMS/busInfoByRouteId"
    params ={'serviceKey' : servicekey, 'lineid': lineId}
    response = requests.get(url, params= params)
    response = response.content.decode('utf-8')
    
    return response

#test
#print(getBusArrivalInfo(185000302))