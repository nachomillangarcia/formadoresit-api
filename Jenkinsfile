pipeline {
    agent {
        kubernetes {
            
            label "docker-build-pod-${env.BUILD_ID}"   // unique label per build
            
            defaultContainer 'builder'   // main container where steps run
            
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: builder
    image: docker:25.0-dind
    securityContext:
      privileged: true
    volumeMounts:
    - name: docker-sock
      mountPath: /var/run
    - name: docker-config
      mountPath: /root/.docker
    resources:
      requests:
        cpu: 1500m
        memory: 2Gi
      limits:
        cpu: 2500m
        memory: 4Gi
    command:
    - dockerd-entrypoint.sh
    args:
    - --mtu=1450
    - --log-level=warn
    
  - name: jnlp
    image: jenkins/inbound-agent:alpine-jdk17
    
  volumes:
  - name: docker-sock
    emptyDir: {}
  - name: docker-config
    emptyDir: {}
'''
        }
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm

            }
        }

        stage('Build Docker image') {
            steps {
                container('builder') {
                    script {
                        // Build the image
                        sh "env"
                        
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}