from email.policy import default
from pyexpat import model
from django.db import models

# 버스정류장 필드
class busStopInfo(models.Model):
    id = models.AutoField(primary_key=True)
    busStopNo = models.IntegerField(default=0)
    busStopId = models.IntegerField(default=0)
    busStopName = models.CharField(max_length=20)
    #gpsX = models.DecimalField(max_digits=15, decimal_places = 12) # max_digits : 숫자에 허용되는 최대 자릿수
    #gpsY = models.DecimalField(max_digits=15, decimal_places = 12) # decimal_places : 숫자와 함께 저장될 소수 자릿수
    gpsX = models.FloatField(default=0)
    gpsY = models.FloatField(default=0)
    busStopType = models.CharField(max_length=10)
    
    # 모델에 __str__() 메소드를 추가하는것은 객체의 표현을 대화식 프롬프트에서 편하게 보려는 이유 말고도, Django 가 자동으로 생성하는 관리 사이트 에서도 객체의 표현이 사용되기 때문
    def __str__(self):
        return self.busStopName
    # 커스텀 메소드
    def returnBusStopId(self):
        return self.busStopId
    def returnGPS(self):
        data = {'lat': self.gpsY, 'log':self.gpsX}
        return data

#id = models.AutoField(primary_key=True, unique = True)