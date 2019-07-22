ssh ubuntu@3.113.55.81 -i Fineract-Satging.pem << EOF
cd FinterraCBS;

git pull;

cd fineract-cn-cause;
bash r.sh;

cd fineract-cn-customer;
bash r.sh;

sudo docker-compose down; sudo docker-compose up -d --build;

EOF


