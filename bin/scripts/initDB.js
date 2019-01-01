/**
 * init proj data
 */
const mongo = new Mongo("localhost:27017")
const mongoDB = mongo.getDB("seckill")

const userC = mongoDB.getCollection("user")
const productC = mongoDB.getCollection("product")
const productInventoryC = mongoDB.getCollection("product.inventory")

let now = new Date();
userC.insert({
  _id: ObjectId("5c2b7097cfc4732a018fa5eb"),
  name: '测试用户',
  createdTime: now,
  lastModTime: now
});

for (let i = 0; i < 20; i++) {
  let prodCreatedTime = new Date();
  let payload = {
    name: '商品' + i,
    price: Math.random() * 100,
    createdTime: prodCreatedTime,
    lastModTime: prodCreatedTime
  };

  productC.insert(payload);
}

