from rest_framework.decorators import api_view
from rest_framework.response import Response
from PIL import Image
import random

from .serializers import AiResponse

@api_view(['POST'])
def test(request):
    datas = ["임서희 돼지", "석사 김용우", "천년묵은 재홍쓰", "먹는다"]
    image_file = Image.open(request.FILES['image'])
    if image_file:
        response = AiResponse(data={
            "code":200,
            "message":"success",
            "data": datas[random.randint(0, 3)]
        })
    else:
        response = AiResponse()
    if response.is_valid(raise_exception=True):
        return Response(response.data)
    else:
        return Response({
            "code" : 400,
            "message": response.errors,
            "data": "",
        }, status=400)