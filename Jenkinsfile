pipeline {
  agent {
    kubernetes {
      yaml '''
        apiVersion: v1
        kind: Pod
        metadata:
          labels:
            some-label: some-label-value
        spec:
          serviceAccount: jenkins
          containers:
          - name: maven
            image: maven:3.9.9-eclipse-temurin-17
            command:
            - cat
            tty: true
          - name: busybox
            image: busybox
            command:
            - cat
            tty: true

          - name: jnlp
            image: image-registry.openshift-image-registry.svc:5000/openshift/jenkins-agent-base:latest
            args: ['\${computer.jnlpmac}', '\${computer.name}']
            workDir: /tmp

        '''
      retries 2
    }
  }
  stages {
    stage('Inspect Containers') {
      steps {
        container('maven') {
            sh "mvn --version"
            sh "java -version"
            sh "env"
            sh "ls"
        }
        container('busybox') {
          sh 'env'
        }
      }
    }
    stage('Run Unit Tests') {
      steps {
        container('maven') {
            sh "mvn test -Dmaven.repo.local=$WORKSPACE/.m2/repository"
        }
  
      }
    }
    stage('Run Unit Tests') {
      steps {
        container("jnlp") {
            sh "oc start-build -F openshift-jee-sample-docker --from-file=target/ROOT.war"
          }
      }
    }
  }
}