# Generated by Django 4.1.2 on 2022-10-15 14:13

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='busStopInfo',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('busStopId', models.IntegerField(default=0)),
                ('busStopName', models.CharField(max_length=20)),
                ('gpsX', models.DecimalField(decimal_places=12, max_digits=15)),
                ('gpsY', models.DecimalField(decimal_places=12, max_digits=15)),
                ('busStopType', models.CharField(max_length=10)),
            ],
        ),
    ]
