# Generated by Django 4.1.2 on 2022-10-16 08:14

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('sunguard', '0003_remove_busstopinfo_gpsy_alter_busstopinfo_gpsx'),
    ]

    operations = [
        migrations.AddField(
            model_name='busstopinfo',
            name='gpsY',
            field=models.FloatField(default=0),
        ),
        migrations.AlterField(
            model_name='busstopinfo',
            name='gpsX',
            field=models.FloatField(default=0),
        ),
    ]