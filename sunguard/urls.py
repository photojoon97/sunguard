from unicodedata import name
from django.urls import path
from . import views

urlpatterns = [
    #path(route, view, kwargs, neme)
    #요청으로 들어온 URL을 보고 일치하는 views의 함수를 호출함.
    #route는 URL 패턴을 갖는 문자열. 요청이 처리될 때, urlpatterns에서 일치하는 url 찾을 때 까지 URL과 패턴을 비교
    #view는 일치하는 패턴을 찾으면 HttpRequest 객체를 첫번째로 인수로 하고, 경로로부터 캡처된 값을 키워드 인수로하여 view함수 호출
    #kwargs 키워드 인수들은 view에 사전형으로 전달
    #name URL에 이름을 지으면, 템플릿을 포함한 Django어디에서나 명확하게 참조 가능
    path('', views.index, name='index'),
    path('arrivalbuses/', views.busArrivalInfo, name='arrival_buses'),
    path('stopinfoapi/', views.busStopInfoApi, name='busStopApi'),
    path('nearstopinfo/', views.showNearStops, name='nearStopApi'),
    path('retrieveLineInfo/', views.retrieveLineInfo, name='retrieveLineInfo'),
]
