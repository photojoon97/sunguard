from atexit import register
from django.contrib import admin
from .models import busStopInfo

# Register your models here.
admin.site.register(busStopInfo)
