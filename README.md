# Microservices POC

# Run project locally with Docker Compose

## Requirements

You will need the following to start project:

* Docker (used 24.0.7 but any version should work).

## Starting the services

```shell
docker compose up -d
```

## Access the services

### Eureka

If you go to http://localhost:8761/ you will see the Eureka dashboard with the list of services registered.

### API usage

To use the APIs, use the following URIs:

* Users API: http://localhost:8088/api/users
* Articles API: http://localhost:8088/api/articles

# Run project locally with Minikube

## Requirements

You will need the following to run the project locally with Minikube :

- Minikube (used v1.32.0) ;
- Kubectl (used v1.28.4).

## Cluster setup

Firstly, start your minikube cluster with the following command :

```bash
minikube start
```

Then, you can create a namespace dedicated to the app with `kubectl` command :

```bash
kubectl create namespace app
```

## Build the Docker images and pass them inside the cluster

For the minikube cluster to start pods based on your host's Docker images, you will need to build the Docker images
first. You can use the `compose-build.yaml` file to build images :

```bash
docker compose -f compose-build.yaml build
```

Or you can build them independently with `docker build` command.

When the images are built, you will need to save them and pass them into the cluster with the following commands :

```bash
docker save microservices-ms-article:latest | (eval $(minikube docker-env) && docker load)
docker save microservices-ms-user:latest | (eval $(minikube docker-env) && docker load)
docker save microservices-ms-discovery:latest | (eval $(minikube docker-env) && docker load)
docker save microservices-ms-gateway:latest | (eval $(minikube docker-env) && docker load)
```

Where `docker save` will create a tarball of the specified image, and `docker load`, combined with the `eval $(minikube
docker-env)`, will load the image inside minikube environment based on the tarball generated in the first part of the
command.

## Apply kubernetes configuration

Then, you will have to apply the kubernetes config files to create all the resources needed with the following command :

```bash
kubectl apply \
-f discovery/kubernetes/deployment.yaml \
-f discovery/kubernetes/service.yaml \
-f database/kubernetes/deployment.yaml \
-f database/kubernetes/service.yaml \
-f article/kubernetes/deployment.yaml \
-f article/kubernetes/service.yaml \
-f user/kubernetes/deployment.yaml \
-f user/kubernetes/service.yaml \
-f gateway/kubernetes/deployment.yaml \
-f gateway/kubernetes/service.yaml \
-n app
```

You can also use the following command to restart all services in the correct order (if needed):

```bash
kubectl rollout restart -n app deployment ms-discovery-deployment
kubectl rollout restart -n app deployment postgres-deployment
kubectl rollout restart -n app deployment ms-user-deployment
kubectl rollout restart -n app deployment ms-article-deployment
kubectl rollout restart -n app deployment ms-gateway-deployment
```

## Access the services

To get the IP of your cluster (which you will use to access the services), use the following command :

```bash
minikube ip
```

It will give you the IP (for example `192.168.50.5`) to use to access the services.

### Eureka

You will have to get the node port of your Eureka service with the following command :

```
kubectl get svc -n app
```

This will give you a result like the following :

```
NAME           TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
database       NodePort   10.103.99.185    <none>        5432:31424/TCP   103m
ms-article     NodePort   10.100.116.161   <none>        8082:31779/TCP   103m
ms-discovery   NodePort   10.102.185.150   <none>        8761:31919/TCP   103m
ms-gateway     NodePort   10.109.16.187    <none>        8088:30981/TCP   103m
ms-user        NodePort   10.108.149.31    <none>        8081:30678/TCP   103m
```

If you watch the "ms-discovery" line, you will see a "PORT(S)" column, with the value `8761:31919/TCP`. The port you
will have to use is `31919`.

So for instance, if we consider our cluster IP is `192.168.50.5`, we can access our Eureka dashboard with the following
URI :

http://192.168.50.5:31919

### API usage

You will have to get the node port of your Gateway service with the following command :

```
kubectl get svc -n app
```

This will give you a result like the following :

```
NAME           TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
database       NodePort   10.103.99.185    <none>        5432:31424/TCP   103m
ms-article     NodePort   10.100.116.161   <none>        8082:31779/TCP   103m
ms-discovery   NodePort   10.102.185.150   <none>        8761:31919/TCP   103m
ms-gateway     NodePort   10.109.16.187    <none>        8088:30981/TCP   103m
ms-user        NodePort   10.108.149.31    <none>        8081:30678/TCP   103m
```

If you watch the "ms-gateway" line, you will see a "PORT(S)" column, with the value `8088:30981/TCP`. The port you will
have to use is `30981`.

So for instance, if we consider our cluster IP is `192.168.50.5`, we can access our APIs with the following URI :

http://192.168.50.5:30981

To use the APIs, use the following URIs (where `192.168.50.5` is our cluster IP and `30981` the port where the gateway
is exposed):

* Users API: http://192.168.50.5:30981/api/users
* Articles API: http://192.168.50.5:30981/api/articles

# Resources

* https://www.javainuse.com/spring/cloud-gateway-eureka
* https://cloud.spring.io/spring-cloud-netflix/multi/multi__circuit_breaker_hystrix_clients.html
* https://cloud.spring.io/spring-cloud-gateway/reference/html/
* https://stackoverflow.com/questions/59171993/eureka-client-getting-registered-with-unknown-urls
* https://stackoverflow.com/questions/62229903/eureka-service-always-registers-instances-with-host-name-even-though-i-put-pref