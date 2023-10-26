from django.urls import path
from . import views

app_name = 'ai_app'

urlpatterns = [
    path('translate/', views.test)
]
