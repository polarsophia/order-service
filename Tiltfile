custom_build(
    ref='library/order-service',
    command='gradlew bootBuildImage --imageName %EXPECTED_REF%',
    deps=['build.gradle', 'src']
)

k8s_yaml(['k8s/app/order-service.yml'])

k8s_resource('order-service', port_forwards=['8090'])
