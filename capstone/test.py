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


time = dt.datetime.now()
now = time.hour
now_str = ''
print('now\' type : ', type(now))
print('now : ', now)
if now >= 9 and now < 12:
    now_str = '09'
elif now >= 12 and now < 15:
    now_str = '12'
elif now >= 15 and now < 18:
    now_str = '15'
elif now >= 18:
    now_str = '18'
else:
    now_str = time.strftime('%H')
print('현재 시간은 : ', now_str)
"""
for i in range(0,25):
    now = str(i)
    print(type(now))
    if i >= 9 and i < 12:
        now_str = '09'
        print('현재 시간 : ' + now + '시는 ' + now_str + ' class')
    elif i >= 12 and i < 15:
        now_str = '12'
        print('현재 시간 : ' + now + '시는 ' + now_str + ' class')
    elif i >= 15 and i < 18:
        now_str = '15'
        print('현재 시간 : ' + now + '시는 ' + now_str + ' class')
    elif i >= 18:
        now_str = '18'
        print('현재 시간 : ' + now + '시는 ' + now_str + ' class')
    else:
        now_str = str(i)
        print('현재 시간 : ' + now + '시는 ' + now_str + ' class')
"""