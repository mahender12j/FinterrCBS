
git checkout staging;

echo "checkout to staging";

git merge dev;


echo "merge to dev";

git push origin staging;

echo "push to staging";


ssh ubuntu@3.113.55.81 -i Fineract-Satging.pem << EOF

echo "SSH access granted to staging ubuntu";

cd FinterraCBS;

echo "CD to root";

 git pull ssh staging;

echo "PULL Staging from the server";

# cd fineract-cn-cause;
# bash r.sh;

# cd fineract-cn-customer;
# bash r.sh;

# sudo docker-compose down; sudo docker-compose up -d --build;

EOF

echo "checkout back to dev";
git checkout dev;