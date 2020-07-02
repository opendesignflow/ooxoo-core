
// Start Build Step function
def transformIntoStep(jobFullName) {
    return {
       build job: jobFullName , wait: false, propagate: false
    }
}

// OOXOO
node {

    //-- Github trigger
    properties([pipelineTriggers([[$class: 'GitHubPushTrigger']])])

    //-- JDK
    jdk = tool name: 'adopt-jdk11'
    env.JAVA_HOME = "${jdk}"

    //-- Maven
    def mvnHome = tool 'maven3'
    mavenOptions="-B -U -up"

    stage('Clean') {
      checkout scm
      sh "${mvnHome}/bin/mvn ${mavenOptions} clean"
    }

  stage('Build & Test') {
    sh "${mvnHome}/bin/mvn ${mavenOptions}  -DskipTests=false install"
    //junit '**/target/surefire-reports/TEST-*.xml'
  }


  if (env.BRANCH_NAME == 'dev' || env.BRANCH_NAME == 'master') {
    
    stage('Deploy') {
     /*configFileProvider(
          [configFile(fileId: '040c946b-486d-4799-97a0-e92a4892e372', variable: 'MAVEN_SETTINGS')]) {
          //sh 'mvn -s $MAVEN_SETTINGS clean package'
          mavenOptions="$mavenOptions -s $MAVEN_SETTINGS"
  
          sh "${mvnHome}/bin/mvn ${mavenOptions} -DskipTests=true deploy"
      }*/
      sh "${mvnHome}/bin/mvn ${mavenOptions} -DskipTests=true deploy"
      step([$class: 'ArtifactArchiver', artifacts: '**/ooxoo-core/target/*.jar', fingerprint: true])
      step([$class: 'ArtifactArchiver', artifacts: '**/maven-ooxoo-plugin/target/*.jar', fingerprint: true])
    }

    // Trigger sub builds on dev
    if (env.BRANCH_NAME == 'dev') {
      stage('Downstream') {

        def downstreams = ['../ubroker-core/dev','../indesign/dev']
        def stepsForParallel = [:]
        for (x in downstreams) {
          def ds = x 
          stepsForParallel[ds] = transformIntoStep(ds) 
        }
      
        parallel stepsForParallel

      }

    }
    // EOF Downstream

  } else {
    
    stage('Package') {
        sh "${mvnHome}/bin/mvn ${mavenOptions} -DskipTests=true package"
        step([$class: 'ArtifactArchiver', artifacts: '**/ooxoo-core/target/*.jar', fingerprint: true])
        step([$class: 'ArtifactArchiver', artifacts: '**/maven-ooxoo-plugin/target/*.jar', fingerprint: true])
    }
  
  }

  


}
