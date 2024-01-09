/* `buildPlugin` step provided by: https://github.com/jenkins-infra/pipeline-library */

buildPlugin(
  forkCount: '1C', 
  useContainerAgent: true, 
  configurations: [
    [platform: 'linux', jdk: 21],
    [platform: 'windows', jdk: 21],
    [platform: 'linux', jdk: 17],
    [platform: 'windows', jdk: 17],
    [platform: 'linux', jdk: 11],
    [platform: 'windows', jdk: 11],
])
