// OOXOO
node {
 
  properties([pipelineTriggers([[$class: 'GitHubPushTrigger']])])
  def mvnHome = tool 'maven3'

  stage('Clean') {
    checkout scm
    sh "${mvnHome}/bin/mvn -B clean"
  }

  stage('Build') {
    sh "${mvnHome}/bin/mvn -B  compile test-compile"
  }

  stage('Test') {
    sh "${mvnHome}/bin/mvn -B -Dmaven.test.failure.ignore test"
    junit '**/target/surefire-reports/TEST-*.xml'
  }

  stage('Deploy') {
      sh "${mvnHome}/bin/mvn -B -DskipTests=true deploy"
      step([$class: 'ArtifactArchiver', artifacts: '**/ooxoo-core/target/*.jar', fingerprint: true])
      step([$class: 'ArtifactArchiver', artifacts: '**/maven-ooxoo-plugin/target/*.jar', fingerprint: true])
  }


}
