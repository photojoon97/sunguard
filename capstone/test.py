import requests
from xml.etree import ElementTree

busServicekey = "NUg3JoF3qG4D0ta4dKvgz9lo4SMpZ03u1Rh1SLHQZcJUEDitfcC3vNeKGjMqVr9dW45y52Z9GWj2yQsMeggVLQ=="

def arrivalBus(busStopId):
    print("정류장 ID : ", busStopId)
    url = "http://apis.data.go.kr/6260000/BusanBIMS/stopArrByBstopid"
    params ={'Bstopid': busStopId, "serviceKey" : busServicekey}

    response = requests.get(url, params)
    print(response.content.decode('utf-8'))

def getLineInfo(lineId):
    nodeLine = []
    #response까지 init 함수로 묶기
    url = "http://apis.data.go.kr/6260000/BusanBIMS/busInfoByRouteId"
    params ={'serviceKey' : busServicekey, 'lineid': lineId}
    response = requests.get(url, params= params)
    response = response.content.decode('utf-8')
    print(response)
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

#arrivalBus(174390202)
arrivalBus(505780000)
#getLineInfo(5200179000)