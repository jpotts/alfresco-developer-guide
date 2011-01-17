<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:pr="http://www.someco.com/corp/pr"
	xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
	exclude-result-prefixes="xhtml pr fn">

	<xsl:output method="html" encoding="UTF-8" indent="yes"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

	<xsl:template match="/">
		<xsl:variable name="pressReleaseList"
			select="alf:parseXMLDocuments('press-release', '/news/press-releases/data')" />
		<html>
			<head>
				<title>
					Press Releases BASE | Open Source Ecommerce and
					Content Syndication Solutions - Optaros
				</title>
				<meta name="description"
					content="Web applications assembled from open source software for business agility">
				</meta>
				<meta name="keywords"
					content="open source, open source software, ecommerce software, ecommerce solution, media widget, ecommerce consulting, ecommerce marketing strategy, content syndication, open source ecommerce, online advertising, advertising online">
				</meta>
				<link rel="alternate"
					href="http://www.optaros.com/blog/feed" type="application/rss+xml"
					class="rss" title="Optaros RSS">
				</link>
				<style type="text/css" media="all">@import "/someco/css/reset.css";</style>
				<style type="text/css" media="all">@import "/someco/css/global.css";</style>
				<style type="text/css" media="all">@import "/someco/css/tabs.css";</style>
				<style type="text/css" media="all">@import "/someco/css/style.css";</style>
				<script type="text/javascript" src="/someco/javascript/jquery.js"></script><script type="text/javascript" src="/someco/javascript/prototype.js"></script><script type="text/javascript" src="/someco/javascript/behaviour.js"></script><script type="text/javascript" src="/someco/javascript/events.js"></script><script type="text/javascript" src="/someco/javascript/scriptaculous.js"></script><script type="text/javascript" src="/someco/javascript/effects.js"></script><!--[if IE 6]><link rel="stylesheet" type="text/css" href="/someco/css/iespecific.css" /><![endif]--><script type="text/javascript" src="/someco/javascript/news.js"></script><script type="text/javascript">
					Event.observe(window,'load',function(){
					initNewsPage('/'); });
				</script>
			</head>

			<body>
				<!-- page-news.tpl -->
				<div id="utilsNavContainer">
					<div id="utils">
						<div class="f-left">
							<ul>
								<li>
									<a href="http://www.optaros.com/"
										class="selected">
										optaros.com
									</a>
								</li>
								<li>
									<a
										href="http://www.eosdirectory.com/" target="_blank">
										EOS Directory
									</a>
								</li>

							</ul>
						</div><!-- .f-left -->
						<div class="f-right">
							<ul>
								<li>
									<a
										href="http://www.optaros.com/blog/feed" id="rss"
										onclick="pageTracker._trackPageview('/tracking/feed');">
										RSS
									</a>
								</li>
							</ul>
							<div id="login"></div><!-- #login -->

						</div><!-- .f-right -->
					</div><!-- #utils -->
				</div><!--  #utilsNavContainer -->
				<div id="pageContainer">
					<div id="mh">
						<div id="mhLeft">
							<a href="http://www.optaros.com/">
								<img
									src="/someco/images/someco-logo-two-rows.png" alt="Home" />
							</a>
						</div>
						<div id="mhRight">
							<div id="navContainer">

								<div class="navColumn one">
									<h2>Solutions</h2>
									<ul class="menu">
										<li>
											<a
												href="http://www.optaros.com/clients" title="Clients">
												Clients
											</a>
										</li>
										<li>
											<a
												href="http://www.optaros.com/clients/swisscom-mobile-labs"
												title="Case Studies ">
												Case Studies
											</a>
										</li>
										<li>
											<a
												href="http://www.optaros.com/industry-solutions"
												title="Industry Solutions ">
												Industry Solutions
											</a>
										</li>
									</ul>
								</div>
								<div class="navColumn two">

									<h2>Approach</h2>
									<ul class="menu">
										<li>
											<a
												href="http://www.optaros.com/user-experience">
												User Experience
											</a>
										</li>
										<li>
											<a
												href="http://www.optaros.com/assembly-methodology">
												Assembly Methodology
											</a>
										</li>
									</ul>
								</div>
								<div class="navColumn three">
									<h2>Expertise</h2>
									<ul class="menu">

										<li>
											<a
												href="http://www.optaros.com/next-generation-internet"
												title="Next Generation Internet">
												Next Generation Internet
											</a>
										</li>
										<li>
											<a
												href="http://www.optaros.com/open-source/partners"
												title="Open Source/Partners">
												Open Source/Partners
											</a>
										</li>
										<li>
											<a
												href="http://www.optaros.com/blogs" title="More...">
												More...
											</a>
										</li>
									</ul>
								</div>
								<div class="navColumn four">
									<h2>Company</h2>
									<ul class="menu">
										<li>
											<a
												href="http://www.optaros.com/people-careers"
												title="People &amp; Careers">
												People &amp; Careers
											</a>
										</li>

										<li>
											<a
												href="http://www.optaros.com/about-us" title="About Us">
												About Us
											</a>
										</li>
										<li class="selected">
											<a
												href="/"
												title="News, Events &amp; Publications">
												News, Events &amp; Pubs
											</a>
										</li>
									</ul>
								</div>
								<div class="navColumn five">
									<h2>Search</h2>
									<div id="top_search">
										<form action="/search/node"
											accept-charset="UTF-8" method="post"
											id="search-theme-form">
											<div>
												<input maxlength="128"
													name="search_theme_form_keys"
													id="edit-search_theme_form_keys" size="5" value=""
													title="Enter the terms you wish to search for."
													class="form-text" type="text" />
												<input name="op"
													value="Go" type="submit" />

												<input name="form_id"
													id="edit-search-theme-form" value="search_theme_form"
													type="hidden" />
												<input name="form_token"
													id="a-unique-id" value="603f3a77f96c8d785c90e12c2ec117a3"
													type="hidden" />

											</div>
										</form>
									</div>
									<span class="contact_menu_item">
										<a
											href="http://www.optaros.com/offices/us-corporate-headquarters"
											title="Locations">
											Locations
										</a>
									</span>
									<span>|</span>
									<span class="contact_menu_item">
										<a
											href="http://www.optaros.com/customer/register?contact"
											title="Contact">
											Contact
										</a>
									</span>
								</div>
								<div class="clearfix"></div>
							</div>
							<div class="clearfix"></div>

						</div><!-- #mhRight -->
						<div class="clearfix"></div>
					</div>
					<!-- #mh -->
					<div id="bdPage">
						<div class="clearfix"></div>
						<div id="featureContainer">
							<div>
								<h1 id="pageTitle">
									News, Events &amp; Pubs
								</h1>

								<div id="topFiles"></div>
							</div>
						</div>
						<div class="clearfix"></div>
						<div id="featureContent">
							<div id="featureFooter">
								<div id="sidebar-left">
									<ul class="menu">
										<li>
											<a href="http://www.optaros.com/news/articles">Articles</a>
										</li>
										<li>
											<a href="http://www.optaros.com/news/presentations">Presentations</a>
										</li>
										<li class="selected">
											<a href="/news/press-releases" class="active">Press Releases</a>
										</li>
										<li>
											<a href="http://www.optaros.com/news/recorded-webinars">Recorded Webinars</a>
										</li>
										<li>
											<a href="http://www.optaros.com/news/upcoming-events">Upcoming Events</a>
										</li>
										<li>
											<a href="/">White Papers &amp; Reports</a>
										</li>
									</ul>
									<div class="clearfix"></div>
								</div>
								<div id="mainContent" class="fifty-fifty news">
									<div class="content-left">
										<div id="tertiaryMenu">
											<div class="archive-dropdown" id="select-years">
												<h3></h3>
												<div class="float-right">
													<form name="year_form" action="" method="post">
														<label>Archives</label>
														<select	onchange="document.year_form.submit()" name="year">
															<option	value="" selected="selected">- year -</option>
															<option	value="2008">2008</option>
															<option value="2007">2007</option>
															<option	value="2006">2006</option>
															<option	value="2005">2005</option>
															<option value="2004">2004</option>
														</select>
													</form>
												</div>
												<div class="clearfix"></div>
											</div>
											<div class="clearfix"></div>
											<div id="arrowMenu">
												<ul class="menu">
													<xsl:for-each select="$pressReleaseList">
														<xsl:variable name="selectedVar">
															<xsl:choose>
																<xsl:when test="position() = 1">selected</xsl:when>
																<xsl:otherwise>leaf</xsl:otherwise>
															</xsl:choose>
														</xsl:variable>															
														<li	class="{$selectedVar}">
															<xsl:variable name="fileNameFixed"><xsl:call-template name="fixFileName"><xsl:with-param name="fileName"><xsl:value-of select="@alf:file_name" /></xsl:with-param></xsl:call-template></xsl:variable>
															<a href="{$fileNameFixed}">
																<xsl:value-of select="pr:title" />
																<span class="bioListTitle"></span>
															</a>
														</li>
													</xsl:for-each>
												</ul>
											</div>
										</div>
										<!-- #tertiaryMenu -->
									</div>
									<div id="newsContent"
										class="content-right">
										<div class="node">
											<h2>
												<xsl:value-of select="$pressReleaseList[1]/pr:title" />
											</h2>
											<p></p><p></p>
											<xsl:value-of select="$pressReleaseList[1]/pr:body" />
											<div class="clearfix"></div><!-- .clearfix -->
										</div><!-- node -->
										<div class="clearfix"></div><!-- .clearfix -->
									</div>
								</div>
								<!-- #mainContent -->
								<div class="clearfix"></div>
							</div>

							<!-- #featureFooter -->
						</div>
					</div>
					<!-- #bdPage -->
				</div>
				<!-- #page-container -->
				<div id="ft">
					<ul class="footerText">
						<li>
							<a href="http://www.optaros.com/sitemap" class="link">Site map</a>
						</li>
						<li>|</li>
						<li>
							<a href="http://www.optaros.com/terms" class="link">Terms of Use</a>
						</li>
						<li>|</li>
						<li>
							<a href="http://www.optaros.com/privacy" class="link">Privacy Policy</a>
						</li>
						<li class="floatR">© Copyright 2008 Optaros, Inc. All Rights Reserved.</li>
					</ul>
				</div>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template name="fixFileName">
		<xsl:param name="fileName" />
		<xsl:value-of select="concat(substring-before($fileName, '.xml'), '.html')" />
	</xsl:template>
	
</xsl:stylesheet>