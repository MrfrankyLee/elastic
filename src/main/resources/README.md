### docker安装elasticsearch
#### 1.设置max_map_count不能启动es会启动不起来
查看max_map_count的值 默认是65530
````
cat /proc/sys/vm/max_map_count
````
重新设置max_map_count的值
````
sysctl -w vm.max_map_count=262144
````
####2.下载镜像并运行
````
# 拉取镜像(本次选用的7.4.0版本)
docker pull elasticsearch:7.4.0
````

````
# 启动镜像
docker run --name elasticsearch -p 9200:9200 -p 9300:9300 \
-e "discovery.type=single-node" \
-v /mydata/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
-v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
-d elasticsearch:7.4.0
````
####3.浏览器访问ip:9200 如果出现以下界面就是安装成功
````
{
  "name" : "65e86c7acd27",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "ko6Dq2jDS4ClzbsuNAc7bQ",
  "version" : {
    "number" : "7.4.0",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "22e1767283e61a198cb4db791ea66e3f11ab9910",
    "build_date" : "2019-09-27T08:36:48.569419Z",
    "build_snapshot" : false,
    "lucene_version" : "8.2.0",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
````