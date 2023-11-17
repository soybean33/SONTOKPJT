from rest_framework import serializers

class AiResponse(serializers.Serializer):
    code = serializers.IntegerField()
    message = serializers.CharField(max_length=100)
    data = serializers.CharField(max_length=200)