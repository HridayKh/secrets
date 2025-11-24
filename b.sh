clear
cd secrets-react
npm run build
cd ..
rm -f secrets/src/main/webapp/index.html
rm -rf secrets/src/main/webapp/assets
cp -r secrets-react/dist/* secrets/src/main/webapp/
rm -rf secrets-react/dist
cd secrets
bash run.sh