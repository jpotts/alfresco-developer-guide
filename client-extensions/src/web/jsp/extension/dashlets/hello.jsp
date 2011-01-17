<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>
<r:webScript scriptUrl="/wcs/someco/helloworld?name=#{NavigationBean.currentUser.userName}" />