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
    stage('Trigger docker build') {
      steps {
        container("jnlp") {
            script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            // Trigger the build from the local directory 
                            // (or let OpenShift pull from Git)
                            def buildObj = openshift.selector("bc", "my-app-name").startBuild()
                            
                            // Stream logs to Jenkins console and wait for completion
                            buildObj.logs('-f')
                            
                            // Ensure the build succeeded
                            timeout(5) { 
                                buildObj.untilEach(1) {
                                    return it.object().status.phase == "Complete"
                                }
                            }
                        }
                    }
          }
      }
    }
  }
}