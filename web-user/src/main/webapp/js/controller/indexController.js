//首页控制器
app.controller('indexController',function($scope,loginService,$controller){
    //继承baseController
    $controller('baseController',{$scope:$scope});

    $scope.showName=function(){
			loginService.showName().success(
					function(response){
						$scope.loginName=response.loginName;
					}
			);
	}

});