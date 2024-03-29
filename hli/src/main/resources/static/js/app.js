$(function() {
    $('#side-menu').metisMenu();
});

var app = angular.module('app', [
    'ngRoute', 'ui.bootstrap', 'ngCookies', 'toaster', 'ngAnimate', "xeditable", "ngSanitize", "ngCsv"
]);

app.run(['$rootScope', '$cookieStore', '$http', 'toaster', function($rootScope, $cookieStore, $http, toaster) {
  	$rootScope.login_url = "/index.html";

  	$rootScope.refreshToken = function(new_token) {
		$rootScope.auth_token = new_token;

		var auth_info = {auth_token : $rootScope.auth_token, role_id : $rootScope.role_id};

		$cookieStore.put("auth_info", auth_info);

		$http.defaults.headers.post['X-Auth'] = $rootScope.auth_token;
  	};

  	$rootScope.logout = function() {
  		$rootScope.auth_token = null;
  		$cookieStore.remove("auth_info");
  		$rootScope.pop('error', 'LogOut', '세션이 만료되었습니다.', 3000);
  	}

  	$rootScope.pop = function(type, title, content, time) {
  		toaster.pop(type, title, content, time);
  	}

  	$rootScope.clear = function() {
  		toaster.clear();
  	}
}]);

app.config( ['$routeProvider', '$locationProvider', '$httpProvider', function ($routeProvider, $locationProvider, $httpProvider) {
	$routeProvider
	.when('/manager', {templateUrl: '/templates/manager.html', controller: 'ManagerCtrl'})
	.when('/goods', {templateUrl: '/templates/goods.html', controller: 'GoodsCtrl'})
	.when('/testgoods', {templateUrl: '/templates/testgoods.html', controller: 'TestGoodsCtrl'})
	.when('/provider', {templateUrl: '/templates/provider.html', controller: 'ProviderCtrl'})
	.when('/seller', {templateUrl: '/templates/seller.html', controller: 'SellerCtrl'})
	.when('/send', {templateUrl: '/templates/send.html', controller: 'SendCtrl'})
	.when('/testsend', {templateUrl: '/templates/testsend.html', controller: 'TestSendCtrl'})
	
	//$locationProvider.html5Mode(false);
	//$locationProvider.hashPrefix('!');

	$httpProvider.defaults.headers.post['X-Auth'] = "";
}]);

app.service('MainSvc', function($http) {
	this.getLogin = function(login) {
		return $http.post('/api/getLogin', login);
	}
})

app.service('ManagerSvc', function($http) {
	this.getManagerList = function(admin) {
		return $http.post('/admin/api/getManagerList', admin);
	}
	this.addManager = function(admin) {
		return $http.post('/admin/api/addManager', admin);
	}
	this.modifyManager = function(admin) {
		return $http.post('/admin/api/modifyManager', admin);
	}
	this.removeManager = function(admin) {
		return $http.post('/admin/api/removeManager', admin);
	}
});

app.service('GoodsSvc', function($http) {
	this.getGoodsList = function(search) {
		return $http.post('/admin/api/getGoodsList', search);
	}
	this.modifyGoodsCommission = function(goods) {
		return $http.post('/admin/api/modifyGoodsCommission', goods);
	}
});

app.service('SellerSvc', function($http) {
	this.getSellerList = function(admin) {
		return $http.post('/admin/api/getSellerList', admin);
	}
	this.addSeller = function(admin) {
		return $http.post('/admin/api/addSeller', admin);
	}
	this.modifySeller = function(admin) {
		return $http.post('/admin/api/modifySeller', admin);
	}
	this.removeSeller = function(admin) {
		return $http.post('/admin/api/removeSeller', admin);
	}
	this.getGoodsListOfSeller = function getGoodsListOfSeller (search) {
		return $http.post('/admin/api/getGoodsOfSeller', search);
	}
	this.modifyMapSellerGoods = function modifyMapSellerGoods (map) {
		return $http.post('/admin/api/modifyMapSellerGoods', map);
	}
});

app.service('SendSvc', function($http) {
	this.getSendList = function(search) {
		return $http.post('/admin/api/getSendList', search);
	}

	this.resend = function(sendReq) {
		return $http.post('/api/resendCoupon', sendReq);
	}
	
	this.getTestSendList = function(search) {
		return $http.post('/admin/api/getTestSendList', search);
	}

	this.testResend = function(sendReq) {
		return $http.post('/dev/api/resendCoupon', sendReq);
	}
});


app.directive('calendar', function () {
    return {
        require: 'ngModel',
        link: function (scope, el, attr, ngModel) {
            $(el).datepicker({
                dateFormat: 'yy-mm-dd',
                onSelect: function (dateText) {
                    scope.$apply(function () {
                        ngModel.$setViewValue(dateText);
                    });
                }
            });
        }
    };
});

app.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
                scope.$apply(function (){
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    };
});

app.controller('MainCtrl', ['$scope', '$http', '$rootScope', '$cookieStore', 'MainSvc', function ($scope, $http, $rootScope, $cookieStore, MainSvc) {

	
}]);

app.controller('ManagerCtrl', ['$scope', '$rootScope', '$window', '$cookieStore', 'ManagerSvc', function ($scope, $rootScope, $window, $cookieStore, AdminSvc) {
	$scope.admins = [];
	
	$scope.currentPageAdmin = 1;
	$scope.totalAdminListCount = 0;

	$scope.admin_mode = "";
	$scope.admin_mode_text = "관리자 추가";

	$scope.roles = [
		{code: 1, name: "슈퍼관리자"},
		{code: 2, name: "관리자"},
		{code: 3, name: "사용자"}
	];


	$scope.getManagerList = function(){
		AdminSvc.getManagerList({start_index:($scope.currentPageAdmin - 1) * 10, page_size:10})
		.success(function(admins, status, headers) {
			$rootScope.refreshToken(headers('X-Auth'));
			$scope.admins = admins.data;
			$scope.totalAdminListCount = admins.total;

			$scope.clearAdmin();
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.getManagerList();

	$scope.clearAdmin = function() {
		$scope.admin_mode = "";
		$scope.admin_mode_text = "관리자 추가";

		$scope.id = null;
		$scope.pass = null;
		$scope.name = null;
		$scope.role_id = "";
	}

	$scope.adminListPageChanged = function() {
		$scope.getManagerList();
	};

	$scope.editAdmin = function(admin) {
		$scope.manager_id = admin.manager_id;
		$scope.admin_mode = "edit";
		$scope.admin_mode_text = "관리자 수정";

		$scope.id = admin.id;
		$scope.pass = admin.pass;
		$scope.name = admin.name;
		$scope.role_id = admin.role_id;
	}

	$scope.addAdmin = function() {
		var admin = {
			id: $scope.id,
			pass: $scope.pass,
			name: $scope.name,
			role_id: $scope.role_id
		}

		AdminSvc.addManager(admin)
		.success(function(data){
			$scope.clearAdmin();
			$scope.getManagerList();
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.modifyAdmin = function() {
		var admin = {
			manager_id: $scope.manager_id,
			name: $scope.name,
			role_id: $scope.role_id
		}
		
		AdminSvc.modifyManager(admin)
		.success(function(data){
			$scope.clearAdmin();
			$scope.getManagerList();
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.deleteAdmin = function(admin) {
		if ($window.confirm("삭제하시겠습니까?")) {	
			AdminSvc.removeManager(admin)
			.success(function(result) {
				$scope.currentPageAdmin = 1;
				
				$scope.clearAdmin();
				$scope.getManagerList();
			}).error(function(data, status) {
				if (status == 401) {
					$rootScope.logout();
				} else {
					alert("error : " + data.message);
				}
			});
		};
	}
}]);

app.controller('GoodsCtrl', ['$scope', '$rootScope', '$window', '$cookieStore', 'GoodsSvc', function ($scope, $rootScope, $window, $cookieStore, GoodsSvc) {
	$scope.goodslist = [];
	
	$scope.currentPageGoods = 1;
	$scope.totalGoodsListCount = 0;
	
	$scope.providers = [
	        		{code: 1, name: "M12"},
	        		{code: 2, name: "Coup"}
	        	];

	$scope.getGoodsList = function(){
		GoodsSvc.getGoodsList({start_index:($scope.currentPageGoods - 1) * 10, page_size:10, isReal: true})
		.success(function(goodslist, status, headers) {
			$rootScope.refreshToken(headers('X-Auth'));
			$scope.goodslist = goodslist.data;
			$scope.totalGoodsListCount = goodslist.total;
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.getGoodsList();

	$scope.goodsListPageChanged = function() {
		$scope.getGoodsList();
	};
	
	$scope.viewDetail = function(goods) {
		$scope.goods_name = goods.goods_name;
		$scope.sell_price = goods.sell_price;
		$scope.market_price = goods.market_price;
		$scope.adj_price = goods.adj_price;
		$scope.goods_code = goods.goods_code;
		$scope.thumbnail = goods.thumbnail;
		$scope.goods_info = goods.goods_info;
		$scope.use_note = goods.use_note;
		$scope.use_term = goods.use_term;
	}

	$scope.modifyGoodsCommission = function(data, goods_id) {
		data.goods_id = goods_id;

		GoodsSvc.modifyGoodsCommission(data)
		.success(function(datas, status, headers) {
			$rootScope.refreshToken(headers('X-Auth'));
		}).error(function(data,status){
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

}]);

app.controller('TestGoodsCtrl', ['$scope', '$rootScope', '$window', '$cookieStore', 'GoodsSvc', function ($scope, $rootScope, $window, $cookieStore, GoodsSvc) {
	$scope.goodslist = [];
	
	$scope.currentPageGoods = 1;
	$scope.totalGoodsListCount = 0;
	
	$scope.providers = [
	        		{code: 1, name: "M12"},
	        		{code: 2, name: "Coup"}
	        	];

	$scope.getGoodsList = function(){
		GoodsSvc.getGoodsList({start_index:($scope.currentPageGoods - 1) * 10, page_size:10, isReal: false})
		.success(function(goodslist, status, headers) {
			$rootScope.refreshToken(headers('X-Auth'));
			$scope.goodslist = goodslist.data;
			$scope.totalGoodsListCount = goodslist.total;
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.getGoodsList();

	$scope.goodsListPageChanged = function() {
		$scope.getGoodsList();
	};
	
	$scope.viewDetail = function(goods) {
		$scope.goods_name = goods.goods_name;
		$scope.sell_price = goods.sell_price;
		$scope.market_price = goods.market_price;
		$scope.adj_price = goods.adj_price;
		$scope.goods_code = goods.goods_code;
		$scope.thumbnail = goods.thumbnail;
		$scope.goods_info = goods.goods_info;
		$scope.use_note = goods.use_note;
		$scope.use_term = goods.use_term;
	}

}]);

app.controller('SellerCtrl', ['$scope', '$rootScope', '$window', '$cookieStore', 'SellerSvc', function ($scope, $rootScope, $window, $cookieStore, SellerSvc) {
	$scope.sellers = [];
	
	$scope.currentPageSeller = 1;
	$scope.totalSellerListCount = 0;

	$scope.seller_mode = "";
	$scope.seller_mode_text = "판매업체 추가";

	$scope.sale_status_list = [
		{code: 1, name: "대기중"},
		{code: 2, name: "진행중"},
		{code: 3, name: "중지"}
	];

	$scope.currentPageGoods = 1;
	$scope.totalGoodsListCount = 0;
	$scope.goodslist = null;
	$scope.company_name = null;


	$scope.getSellerList = function(){
		SellerSvc.getSellerList({start_index:($scope.currentPageSeller - 1) * 10, page_size:10, isReal: false})
		.success(function(sellers, status, headers) {
			$rootScope.refreshToken(headers('X-Auth'));
			$scope.sellers = sellers.data;
			$scope.totalSellerListCount = sellers.total;

			$scope.clearSeller();
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.getSellerList();

	$scope.clearSeller = function() {
		$scope.seller_mode = "";
		$scope.seller_mode_text = "판매업체 추가";

		$scope.seller_id = null;
		$scope.company_name = null;
		$scope.mid = null;
		$scope.password = null;
		$scope.person_name = null;
		$scope.contact = null;
		$scope.email = null;
		$scope.allowed_ip = null;
		$scope.sale_status = "";
	}

	$scope.sellerListPageChanged = function() {
		$scope.getSellerList();
	};
	
	$scope.editSeller = function(seller) {
		$scope.seller_id = seller.seller_id;
		$scope.seller_mode = "edit";
		$scope.seller_mode_text = "판매업체 수정";

		$scope.company_name = seller.company_name;
		$scope.mid = seller.mid;
		$scope.password = seller.password;
		$scope.person_name = seller.person_name;
		$scope.contact = seller.contact;
		$scope.email = seller.email;
		$scope.allowed_ip = seller.allowed_ip;
		$scope.sale_status = seller.sale_status;
	}
	
	$scope.addSeller = function() {
		var seller = {
			company_name: $scope.company_name,
			mid: $scope.mid,
			password: $scope.password,
			mid: $scope.mid,
			person_name: $scope.person_name,
			contact: $scope.contact,
			email: $scope.email,
			allowed_ip: $scope.allowed_ip,
			sale_status: $scope.sale_status,
		}

		SellerSvc.addSeller(seller)
		.success(function(data){
			$scope.clearSeller();
			$scope.getSellerList();
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.modifySeller = function() {
		var seller = {
			seller_id: $scope.seller_id,
			company_name: $scope.company_name,
			mid: $scope.mid,
			password: $scope.password,
			person_name: $scope.person_name,
			contact: $scope.contact,
			email: $scope.email,
			allowed_ip: $scope.allowed_ip,
			sale_status: $scope.sale_status,
		}
		
		SellerSvc.modifySeller(seller)
		.success(function(data){
			$scope.clearSeller();
			$scope.getSellerList();
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.deleteSeller = function(seller) {
		if ($window.confirm("삭제하시겠습니까?")) {	
			SellerSvc.removeSeller(seller)
			.success(function(result) {
				$scope.currentPageSeller = 1;
				
				$scope.clearSeller();
				$scope.getSellerList();
			}).error(function(data, status) {
				if (status == 401) {
					$rootScope.logout();
				} else {
					alert("error : " + data.message);
				}
			});
		};
	}

	$scope.editGoods = function(seller) {
		console.log('editGoods');
		$scope.goodslist = null; //초기화
		$scope.company_name = seller.company_name;

		var search = {
			seller_id: seller.seller_id,
			start_index: 0,
			page_size:30
		}

		SellerSvc.getGoodsListOfSeller(search)
		.success(function(datas, status, headers) {
			$rootScope.refreshToken(headers('X-Auth'));
			$scope.goodslist = datas.data;
			$scope.totalGoodsListCount = datas.total;
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}
	
	$scope.use_yn = [
		{value: 0, text: "사용"},
		{value: 1, text: "미사용"},
	];
	
	$scope.modifyMapSellerGoods = function(data, seller_id, goods_id) {
	    data.seller_id = seller_id;
	    data.goods_id = goods_id;
	    SellerSvc.modifyMapSellerGoods(data)
		.success(function(datas, status, headers) {
			$rootScope.refreshToken(headers('X-Auth'));
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	};
	
}]);

app.controller('SendCtrl', ['$scope', '$rootScope', '$window', '$cookieStore', 'SendSvc', function ($scope, $rootScope, $window, $cookieStore, SendSvc) {
	//실발송------------------
	$scope.sendList = [];
	$scope.csvList = [];
	$scope.csvHeader=['발송시간', '판매업체', '상품명', '판매가격', '상품수량', 'tr_id', '수신자번호', '발송결과', '결과메시지'];
	
	$scope.currentPageSend = 1;
	$scope.totalSendListCount = 0;

	$scope.getSendList = function(){
		SendSvc.getSendList({start_index:($scope.currentPageSend - 1) * 10, page_size:10, isReal:true})
		.success(function(datas, status, headers) {
			$rootScope.refreshToken(headers('X-Auth'));
			$scope.sendList = datas.data;
			$scope.totalSendListCount = datas.total;
			
			angular.forEach($scope.sendList, function(send) {
				$scope.csvList.push({created:"'"+send['created'], 
					                 company_name:send['company_name'],
					                 brand_name:send['brand_name'],
									 sell_price:send['sell_price'],
									 goods_count:send['goods_count'],
									 tr_id:send['tr_id'],
									 recv_phone:"'"+send['recv_phone'],
									 result_code:send['result_code'],
									 status_code:send['status_code']
				});

			});
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.getSendList();

	$scope.sendListPageChanged = function() {
		$scope.getSendList();
	};

}]);

app.controller('TestSendCtrl', ['$scope', '$rootScope', '$window', '$cookieStore', 'SendSvc', function ($scope, $rootScope, $window, $cookieStore, SendSvc) {
	//테스트발송-----------
	$scope.sendList = [];
	$scope.csvList = [];
	$scope.csvHeader=['발송시간', '판매업체', '상품명', '판매가격', '상품수량', 'tr_id', '수신자번호', '발송결과', '결과메시지', '쿠폰번호', '핀번호'];
	
	$scope.currentPageSend = 1;
	$scope.totalSendListCount = 0;

	$scope.getSendList = function(){
		SendSvc.getSendList({start_index:($scope.currentPageSend - 1) * 10, page_size:10, isReal:false})
		.success(function(datas, status, headers) {
			$rootScope.refreshToken(headers('X-Auth'));
			$scope.sendList = datas.data;
			$scope.totalSendListCount = datas.total;
			
			angular.forEach($scope.sendList, function(send) {
				$scope.csvList.push({created:"'"+send['created'], 
					                 company_name:send['company_name'],
					                 brand_name:send['brand_name'],
									 sell_price:send['sell_price'],
									 goods_count:send['goods_count'],
									 tr_id:send['tr_id'],
									 recv_phone:"'"+send['recv_phone'],
									 result_code:send['result_code'],
									 status_code:send['status_code'],
									 couponnumber:"'"+send['couponnumber'],
									 pinnumber:"'"+send['pinnumber']
				});

			});
			
		}).error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}

	$scope.getSendList();

	$scope.sendListPageChanged = function() {
		$scope.getSendList();
	};

	$scope.resend = function(sendReq) {
		SendSvc.testResend(sendReq)
		.success(function(datas, status, headers) {

		})
		.error(function(data, status) {
			if (status == 401) {
				$rootScope.logout();
			} else {
				alert("error : " + data.message);
			}
		});
	}
	
}]);
