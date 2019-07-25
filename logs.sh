ssh ubuntu@3.113.55.81 -i Fineract-Satging.pem << EOF

echo "######################## SSH access granted to staging ubuntu ########################";

cd FinterraCBS;
echo "######################## SHOW LOGS ########################";

sudo docker-compose logs --tail=0 --follow

EOF