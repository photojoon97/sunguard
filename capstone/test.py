import requests
import urllib.parse as parse
from xml.etree import ElementTree
import datetime as dt

busServicekey = "NUg3JoF3qG4D0ta4dKvgz9lo4SMpZ03u1Rh1SLHQZcJUEDitfcC3vNeKGjMqVr9dW45y52Z9GWj2yQsMeggVLQ=="

def arrivalBus(busStopId):
    print("정류장 ID : ", busStopId)
    url = "http://apis.data.go.kr/6260000/BusanBIMS/stopArrByBstopid?"
    params ={'bstopid': busStopId, "serviceKey" : busServicekey}

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

def testCase(routeAzimuth, azimuth):
    routeAzimuth = routeAzimuth
    azimuth = azimuth
    recommendSeat = ""
    right = '오른쪽'
    left = '왼쪽'

    if (routeAzimuth - 180) < 0: #진행방향의 오른쪽 범위를 구함
        front = routeAzimuth
        rear = 360 + (routeAzimuth - 180) 
        if (azimuth > front and azimuth < 360 ): #태양의 방위각이 진행방향의 오른쪽 범위에 포함되면
            if azimuth < rear:
                print(1)
                recommendSeat = left
            else:
                print(2)
                recommendSeat = right
        elif (azimuth < front) and azimuth > 0:
            if azimuth < rear:
                print(3)
                recommendSeat = right
            else:
                print(4)
                recommendSeat = left

    elif routeAzimuth - 180 >= 0:
        front =  routeAzimuth
        rear = routeAzimuth - 180
        if (azimuth < front and azimuth < 360):
            if azimuth < rear:
                print(5)
                recommendSeat = left
            else:
                print(6)
                recommendSeat = right
        elif (azimuth > front) and azimuth > 0:
            if azimuth > rear:
                print(7)
                recommendSeat = left
            else:
                print(8)
                recommendSeat = right
    return recommendSeat


"""

"""

azimuth = {
    '09' : 129,
    '12' : 175,
    '15' : 222,
    '18' : 252
    }



for routeAzimuth in range(10, 360, 30):
    for key in azimuth:
        result = testCase(routeAzimuth, azimuth[key])
        print('진행 방향 : ' + str(routeAzimuth) + ', 태양 방위각 : ' + str(azimuth[key]) + ",  결과 : ", result)
        print("=" * 60)
