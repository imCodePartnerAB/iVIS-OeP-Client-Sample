<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:exsl="http://exslt.org/common">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/CKEditor.xsl" />

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
		/ckeditor/ckeditor.js
		/ckeditor/adapters/jquery.js
		/ckeditor/init.js
	</xsl:variable>

	<xsl:variable name="rawSortIndexOptions">
		<option>
			<name>1</name>
			<value>1</value>
		</option>
		<option>
			<name>2</name>
			<value>2</value>
		</option>
		<option>
			<name>3</name>
			<value>3</value>
		</option>
		<option>
			<name>4</name>
			<value>4</value>
		</option>
		<option>
			<name>5</name>
			<value>5</value>
		</option>
		<option>
			<name>6</name>
			<value>6</value>
		</option>
		<option>
			<name>7</name>
			<value>7</value>
		</option>
		<option>
			<name>8</name>
			<value>8</value>
		</option>
		<option>
			<name>9</name>
			<value>9</value>
		</option>
		<option>
			<name>10</name>
			<value>10</value>
		</option>
		<option>
			<name>11</name>
			<value>11</value>
		</option>
		<option>
			<name>12</name>
			<value>12</value>
		</option>
		<option>
			<name>13</name>
			<value>13</value>
		</option>
		<option>
			<name>14</name>
			<value>14</value>
		</option>
		<option>
			<name>15</name>
			<value>15</value>
		</option>
		<option>
			<name>16</name>
			<value>16</value>
		</option>
		<option>
			<name>17</name>
			<value>17</value>
		</option>
		<option>
			<name>18</name>
			<value>18</value>
		</option>
		<option>
			<name>19</name>
			<value>19</value>
		</option>
		<option>
			<name>20</name>
			<value>20</value>
		</option>
		<option>
			<name>21</name>
			<value>21</value>
		</option>
		<option>
			<name>22</name>
			<value>22</value>
		</option>
		<option>
			<name>23</name>
			<value>23</value>
		</option>
		<option>
			<name>24</name>
			<value>24</value>
		</option>
		<option>
			<name>25</name>
			<value>25</value>
		</option>
		<option>
			<name>26</name>
			<value>26</value>
		</option>
		<option>
			<name>27</name>
			<value>27</value>
		</option>
		<option>
			<name>28</name>
			<value>28</value>
		</option>
		<option>
			<name>29</name>
			<value>29</value>
		</option>
		<option>
			<name>30</name>
			<value>30</value>
		</option>
		<option>
			<name>31</name>
			<value>31</value>
		</option>
		<option>
			<name>32</name>
			<value>32</value>
		</option>
		<option>
			<name>33</name>
			<value>33</value>
		</option>
		<option>
			<name>34</name>
			<value>34</value>
		</option>
		<option>
			<name>35</name>
			<value>35</value>
		</option>
		<option>
			<name>36</name>
			<value>36</value>
		</option>
		<option>
			<name>37</name>
			<value>37</value>
		</option>
		<option>
			<name>38</name>
			<value>38</value>
		</option>
		<option>
			<name>39</name>
			<value>39</value>
		</option>
		<option>
			<name>40</name>
			<value>40</value>
		</option>
		<option>
			<name>41</name>
			<value>41</value>
		</option>
		<option>
			<name>42</name>
			<value>42</value>
		</option>
		<option>
			<name>43</name>
			<value>43</value>
		</option>
		<option>
			<name>44</name>
			<value>44</value>
		</option>
		<option>
			<name>45</name>
			<value>45</value>
		</option>
		<option>
			<name>46</name>
			<value>46</value>
		</option>
		<option>
			<name>47</name>
			<value>47</value>
		</option>
		<option>
			<name>48</name>
			<value>48</value>
		</option>
		<option>
			<name>49</name>
			<value>49</value>
		</option>
		<option>
			<name>50</name>
			<value>50</value>
		</option>
		<option>
			<name>51</name>
			<value>51</value>
		</option>
		<option>
			<name>52</name>
			<value>52</value>
		</option>
		<option>
			<name>53</name>
			<value>53</value>
		</option>
		<option>
			<name>54</name>
			<value>54</value>
		</option>
		<option>
			<name>55</name>
			<value>55</value>
		</option>
		<option>
			<name>56</name>
			<value>56</value>
		</option>
		<option>
			<name>57</name>
			<value>57</value>
		</option>
		<option>
			<name>58</name>
			<value>58</value>
		</option>
		<option>
			<name>59</name>
			<value>59</value>
		</option>
		<option>
			<name>60</name>
			<value>60</value>
		</option>
		<option>
			<name>61</name>
			<value>61</value>
		</option>
		<option>
			<name>62</name>
			<value>62</value>
		</option>
		<option>
			<name>63</name>
			<value>63</value>
		</option>
		<option>
			<name>64</name>
			<value>64</value>
		</option>
		<option>
			<name>65</name>
			<value>65</value>
		</option>
		<option>
			<name>66</name>
			<value>66</value>
		</option>
		<option>
			<name>67</name>
			<value>67</value>
		</option>
		<option>
			<name>68</name>
			<value>68</value>
		</option>
		<option>
			<name>69</name>
			<value>69</value>
		</option>
		<option>
			<name>70</name>
			<value>70</value>
		</option>
		<option>
			<name>71</name>
			<value>71</value>
		</option>
		<option>
			<name>72</name>
			<value>72</value>
		</option>
		<option>
			<name>73</name>
			<value>73</value>
		</option>
		<option>
			<name>74</name>
			<value>74</value>
		</option>
		<option>
			<name>75</name>
			<value>75</value>
		</option>
		<option>
			<name>76</name>
			<value>76</value>
		</option>
		<option>
			<name>77</name>
			<value>77</value>
		</option>
		<option>
			<name>78</name>
			<value>78</value>
		</option>
		<option>
			<name>79</name>
			<value>79</value>
		</option>
		<option>
			<name>80</name>
			<value>80</value>
		</option>
		<option>
			<name>81</name>
			<value>81</value>
		</option>
		<option>
			<name>82</name>
			<value>82</value>
		</option>
		<option>
			<name>83</name>
			<value>83</value>
		</option>
		<option>
			<name>84</name>
			<value>84</value>
		</option>
		<option>
			<name>85</name>
			<value>85</value>
		</option>
		<option>
			<name>86</name>
			<value>86</value>
		</option>
		<option>
			<name>87</name>
			<value>87</value>
		</option>
		<option>
			<name>88</name>
			<value>88</value>
		</option>
		<option>
			<name>89</name>
			<value>89</value>
		</option>
		<option>
			<name>90</name>
			<value>90</value>
		</option>
		<option>
			<name>91</name>
			<value>91</value>
		</option>
		<option>
			<name>92</name>
			<value>92</value>
		</option>
		<option>
			<name>93</name>
			<value>93</value>
		</option>
		<option>
			<name>94</name>
			<value>94</value>
		</option>
		<option>
			<name>95</name>
			<value>95</value>
		</option>
		<option>
			<name>96</name>
			<value>96</value>
		</option>
		<option>
			<name>97</name>
			<value>97</value>
		</option>
		<option>
			<name>98</name>
			<value>98</value>
		</option>
		<option>
			<name>99</name>
			<value>99</value>
		</option>
		<option>
			<name>100</name>
			<value>100</value>
		</option>
		<option>
			<name>101</name>
			<value>101</value>
		</option>
		<option>
			<name>102</name>
			<value>102</value>
		</option>
		<option>
			<name>103</name>
			<value>103</value>
		</option>
		<option>
			<name>104</name>
			<value>104</value>
		</option>
		<option>
			<name>105</name>
			<value>105</value>
		</option>
		<option>
			<name>106</name>
			<value>106</value>
		</option>
		<option>
			<name>107</name>
			<value>107</value>
		</option>
		<option>
			<name>108</name>
			<value>108</value>
		</option>
		<option>
			<name>109</name>
			<value>109</value>
		</option>
		<option>
			<name>110</name>
			<value>110</value>
		</option>
		<option>
			<name>111</name>
			<value>111</value>
		</option>
		<option>
			<name>112</name>
			<value>112</value>
		</option>
		<option>
			<name>113</name>
			<value>113</value>
		</option>
		<option>
			<name>114</name>
			<value>114</value>
		</option>
		<option>
			<name>115</name>
			<value>115</value>
		</option>
		<option>
			<name>116</name>
			<value>116</value>
		</option>
		<option>
			<name>117</name>
			<value>117</value>
		</option>
		<option>
			<name>118</name>
			<value>118</value>
		</option>
		<option>
			<name>119</name>
			<value>119</value>
		</option>
		<option>
			<name>120</name>
			<value>120</value>
		</option>
		<option>
			<name>121</name>
			<value>121</value>
		</option>
		<option>
			<name>122</name>
			<value>122</value>
		</option>
		<option>
			<name>123</name>
			<value>123</value>
		</option>
		<option>
			<name>124</name>
			<value>124</value>
		</option>
		<option>
			<name>125</name>
			<value>125</value>
		</option>
		<option>
			<name>126</name>
			<value>126</value>
		</option>
		<option>
			<name>127</name>
			<value>127</value>
		</option>
		<option>
			<name>128</name>
			<value>128</value>
		</option>
		<option>
			<name>129</name>
			<value>129</value>
		</option>
		<option>
			<name>130</name>
			<value>130</value>
		</option>
		<option>
			<name>131</name>
			<value>131</value>
		</option>
		<option>
			<name>132</name>
			<value>132</value>
		</option>
		<option>
			<name>133</name>
			<value>133</value>
		</option>
		<option>
			<name>134</name>
			<value>134</value>
		</option>
		<option>
			<name>135</name>
			<value>135</value>
		</option>
		<option>
			<name>136</name>
			<value>136</value>
		</option>
		<option>
			<name>137</name>
			<value>137</value>
		</option>
		<option>
			<name>138</name>
			<value>138</value>
		</option>
		<option>
			<name>139</name>
			<value>139</value>
		</option>
		<option>
			<name>140</name>
			<value>140</value>
		</option>
		<option>
			<name>141</name>
			<value>141</value>
		</option>
		<option>
			<name>142</name>
			<value>142</value>
		</option>
		<option>
			<name>143</name>
			<value>143</value>
		</option>
		<option>
			<name>144</name>
			<value>144</value>
		</option>
		<option>
			<name>145</name>
			<value>145</value>
		</option>
		<option>
			<name>146</name>
			<value>146</value>
		</option>
		<option>
			<name>147</name>
			<value>147</value>
		</option>
		<option>
			<name>148</name>
			<value>148</value>
		</option>
		<option>
			<name>149</name>
			<value>149</value>
		</option>
		<option>
			<name>150</name>
			<value>150</value>
		</option>
		<option>
			<name>151</name>
			<value>151</value>
		</option>
		<option>
			<name>152</name>
			<value>152</value>
		</option>
		<option>
			<name>153</name>
			<value>153</value>
		</option>
		<option>
			<name>154</name>
			<value>154</value>
		</option>
		<option>
			<name>155</name>
			<value>155</value>
		</option>
		<option>
			<name>156</name>
			<value>156</value>
		</option>
		<option>
			<name>157</name>
			<value>157</value>
		</option>
		<option>
			<name>158</name>
			<value>158</value>
		</option>
		<option>
			<name>159</name>
			<value>159</value>
		</option>
		<option>
			<name>160</name>
			<value>160</value>
		</option>
		<option>
			<name>161</name>
			<value>161</value>
		</option>
		<option>
			<name>162</name>
			<value>162</value>
		</option>
		<option>
			<name>163</name>
			<value>163</value>
		</option>
		<option>
			<name>164</name>
			<value>164</value>
		</option>
		<option>
			<name>165</name>
			<value>165</value>
		</option>
		<option>
			<name>166</name>
			<value>166</value>
		</option>
		<option>
			<name>167</name>
			<value>167</value>
		</option>
		<option>
			<name>168</name>
			<value>168</value>
		</option>
		<option>
			<name>169</name>
			<value>169</value>
		</option>
		<option>
			<name>170</name>
			<value>170</value>
		</option>
		<option>
			<name>171</name>
			<value>171</value>
		</option>
		<option>
			<name>172</name>
			<value>172</value>
		</option>
		<option>
			<name>173</name>
			<value>173</value>
		</option>
		<option>
			<name>174</name>
			<value>174</value>
		</option>
		<option>
			<name>175</name>
			<value>175</value>
		</option>
		<option>
			<name>176</name>
			<value>176</value>
		</option>
		<option>
			<name>177</name>
			<value>177</value>
		</option>
		<option>
			<name>178</name>
			<value>178</value>
		</option>
		<option>
			<name>179</name>
			<value>179</value>
		</option>
		<option>
			<name>180</name>
			<value>180</value>
		</option>
		<option>
			<name>181</name>
			<value>181</value>
		</option>
		<option>
			<name>182</name>
			<value>182</value>
		</option>
		<option>
			<name>183</name>
			<value>183</value>
		</option>
		<option>
			<name>184</name>
			<value>184</value>
		</option>
		<option>
			<name>185</name>
			<value>185</value>
		</option>
		<option>
			<name>186</name>
			<value>186</value>
		</option>
		<option>
			<name>187</name>
			<value>187</value>
		</option>
		<option>
			<name>188</name>
			<value>188</value>
		</option>
		<option>
			<name>189</name>
			<value>189</value>
		</option>
		<option>
			<name>190</name>
			<value>190</value>
		</option>
		<option>
			<name>191</name>
			<value>191</value>
		</option>
		<option>
			<name>192</name>
			<value>192</value>
		</option>
		<option>
			<name>193</name>
			<value>193</value>
		</option>
		<option>
			<name>194</name>
			<value>194</value>
		</option>
		<option>
			<name>195</name>
			<value>195</value>
		</option>
		<option>
			<name>196</name>
			<value>196</value>
		</option>
		<option>
			<name>197</name>
			<value>197</value>
		</option>
		<option>
			<name>198</name>
			<value>198</value>
		</option>
		<option>
			<name>199</name>
			<value>199</value>
		</option>
		<option>
			<name>200</name>
			<value>200</value>
		</option>
		<option>
			<name>201</name>
			<value>201</value>
		</option>
		<option>
			<name>202</name>
			<value>202</value>
		</option>
		<option>
			<name>203</name>
			<value>203</value>
		</option>
		<option>
			<name>204</name>
			<value>204</value>
		</option>
		<option>
			<name>205</name>
			<value>205</value>
		</option>
		<option>
			<name>206</name>
			<value>206</value>
		</option>
		<option>
			<name>207</name>
			<value>207</value>
		</option>
		<option>
			<name>208</name>
			<value>208</value>
		</option>
		<option>
			<name>209</name>
			<value>209</value>
		</option>
		<option>
			<name>210</name>
			<value>210</value>
		</option>
		<option>
			<name>211</name>
			<value>211</value>
		</option>
		<option>
			<name>212</name>
			<value>212</value>
		</option>
		<option>
			<name>213</name>
			<value>213</value>
		</option>
		<option>
			<name>214</name>
			<value>214</value>
		</option>
		<option>
			<name>215</name>
			<value>215</value>
		</option>
		<option>
			<name>216</name>
			<value>216</value>
		</option>
		<option>
			<name>217</name>
			<value>217</value>
		</option>
		<option>
			<name>218</name>
			<value>218</value>
		</option>
		<option>
			<name>219</name>
			<value>219</value>
		</option>
		<option>
			<name>220</name>
			<value>220</value>
		</option>
		<option>
			<name>221</name>
			<value>221</value>
		</option>
		<option>
			<name>222</name>
			<value>222</value>
		</option>
		<option>
			<name>223</name>
			<value>223</value>
		</option>
		<option>
			<name>224</name>
			<value>224</value>
		</option>
		<option>
			<name>225</name>
			<value>225</value>
		</option>
		<option>
			<name>226</name>
			<value>226</value>
		</option>
		<option>
			<name>227</name>
			<value>227</value>
		</option>
		<option>
			<name>228</name>
			<value>228</value>
		</option>
		<option>
			<name>229</name>
			<value>229</value>
		</option>
		<option>
			<name>230</name>
			<value>230</value>
		</option>
		<option>
			<name>231</name>
			<value>231</value>
		</option>
		<option>
			<name>232</name>
			<value>232</value>
		</option>
		<option>
			<name>233</name>
			<value>233</value>
		</option>
		<option>
			<name>234</name>
			<value>234</value>
		</option>
		<option>
			<name>235</name>
			<value>235</value>
		</option>
		<option>
			<name>236</name>
			<value>236</value>
		</option>
		<option>
			<name>237</name>
			<value>237</value>
		</option>
		<option>
			<name>238</name>
			<value>238</value>
		</option>
		<option>
			<name>239</name>
			<value>239</value>
		</option>
		<option>
			<name>240</name>
			<value>240</value>
		</option>
		<option>
			<name>241</name>
			<value>241</value>
		</option>
		<option>
			<name>242</name>
			<value>242</value>
		</option>
		<option>
			<name>243</name>
			<value>243</value>
		</option>
		<option>
			<name>244</name>
			<value>244</value>
		</option>
		<option>
			<name>245</name>
			<value>245</value>
		</option>
		<option>
			<name>246</name>
			<value>246</value>
		</option>
		<option>
			<name>247</name>
			<value>247</value>
		</option>
		<option>
			<name>248</name>
			<value>248</value>
		</option>
		<option>
			<name>249</name>
			<value>249</value>
		</option>
		<option>
			<name>250</name>
			<value>250</value>
		</option>
		<option>
			<name>251</name>
			<value>251</value>
		</option>
		<option>
			<name>252</name>
			<value>252</value>
		</option>
		<option>
			<name>253</name>
			<value>253</value>
		</option>
		<option>
			<name>254</name>
			<value>254</value>
		</option>
		<option>
			<name>255</name>
			<value>255</value>
		</option>
	</xsl:variable>

	<xsl:variable name="sortIndexOptions" select="exsl:node-set($rawSortIndexOptions)/option"></xsl:variable>

	<xsl:template match="Document">
		
		<div class="contentitem">
			<xsl:apply-templates select="SelectProvider"/>
			<xsl:apply-templates select="Configure"/>
		</div>
						
	</xsl:template>
	
	<xsl:template match="SelectProvider">
	
		<xsl:choose>
			<xsl:when test="LoginProviders">
			
				<xsl:apply-templates select="LoginProviders/ProviderConfiguration"/>
			
			</xsl:when>
			<xsl:otherwise>

				<h1><xsl:value-of select="$i18n.NoLoginProviderAvailable.Title"/></h1>
			
				<p><xsl:value-of select="$i18n.NoLoginProviderAvailable.Message"/></p>
			
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:if test="/Document/IsAdmin">
			<div class="floatright marginright clearboth margintop">
				<a href="{/Document/requestinfo/contextpath}{FullAlias}/config" title="{$i18n.ConfigureModule}">
					<xsl:value-of select="$i18n.ConfigureModule"/>
					<xsl:text>&#x20;</xsl:text>
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/spanner.png"/>
				</a>		
			</div>
		</xsl:if>
		
	</xsl:template>	
	
	<xsl:template match="ProviderConfiguration">
	
		<div class="loginprovider bigmargintop">
			
			<xsl:value-of select="description" disable-output-escaping="yes"/>
			
			<form method="GET" action="{/Document/requestinfo/contextpath}{../../FullAlias}/login">
			
				<xsl:if test="../../Redirect">
					<input type="hidden" name="redirect" value="{../../Redirect}"/>
				</xsl:if>
			
				<input type="hidden" name="provider" value="{providerID}"/>
			
				<input type="submit" value="{buttonText}"/>
			
			</form>
			
		</div>
	
	</xsl:template>

	<xsl:template match="Configure">
	
		<h1><xsl:value-of select="$i18n.Configure.Title"/></h1>
		
		<xsl:if test="ValidationErrors">
		
			<p class="error">
				<xsl:value-of select="$i18n.ValidationErrorsPresent"/>
			</p>
		
		</xsl:if>
		
		<form method="POST" action="{/Document/requestinfo/uri}">
		
			<xsl:choose>
				<xsl:when test="LoginProviders">
				
					<xsl:apply-templates select="LoginProviders/ProviderDescriptor"/>
				
				</xsl:when>
				<xsl:otherwise>
					<p>
						<xsl:value-of select="$i18n.NoLoginProvidersFoundInLoginHandler"/>
					</p>
				</xsl:otherwise>
			</xsl:choose>

			<div class="floatright">
			
				<input type="submit" value="{$i18n.SaveChanges}"/>
			
			</div>
		
		</form>
	
	</xsl:template>	
	
	<xsl:template match="ProviderDescriptor">
	
	<div class="full border border-box padding marginbottom">
	
		<xsl:variable name="providerID" select="id"/>
	
		<xsl:variable name="providerConfiguration" select="../../ProviderConfigurations/ProviderConfiguration[providerID = $providerID]"/>
	
		<div class="full">
			<xsl:call-template name="createCheckbox">
				<xsl:with-param name="id" select="id"/>
				<xsl:with-param name="name" select="'providerID'"/>
				<xsl:with-param name="value" select="id"/>
				<xsl:with-param name="class" select="provider"/>
				<xsl:with-param name="requestparameters" select="../../requestparameters"/>
				
				<xsl:with-param name="checked">
				
					<xsl:if test="$providerConfiguration">true</xsl:if>
				
				</xsl:with-param>
				
			</xsl:call-template>
			
			<xsl:value-of select="name"/>		
		</div>
		
		<div class="full" id="form-{providerID}">
		
			<div class="full margintop">
				<strong>
					<xsl:value-of select="$i18n.Description"/>
				</strong>
				
				<xsl:variable name="descriptionFieldname" select="concat(id,'-description')"/>
				
				<xsl:apply-templates select="../../ValidationErrors/validationError[fieldName = $descriptionFieldname]"/>
				
				<xsl:call-template name="createTextArea">
					<xsl:with-param name="name" select="$descriptionFieldname"/>
					<xsl:with-param name="class" select="'htmleditor'"/>
					<xsl:with-param name="value" select="$providerConfiguration/description" />		
					<xsl:with-param name="requestparameters" select="../../requestparameters"/>
				</xsl:call-template>			
			</div>
		
			<div class="full margintop">
				<strong>
					<xsl:value-of select="$i18n.ButtonText"/>
				</strong>
				
				<xsl:variable name="buttonFieldname" select="concat(id,'-button')"/>
				
				<xsl:apply-templates select="../../ValidationErrors/validationError[fieldName = $buttonFieldname]"/>
				
				<xsl:call-template name="createTextField">
					<xsl:with-param name="name" select="$buttonFieldname"/>
					<xsl:with-param name="value" select="$providerConfiguration/buttonText" />
					<xsl:with-param name="requestparameters" select="../../requestparameters"/>
				</xsl:call-template>
			</div>
		
			<div class="full margintop">
				<strong>
					<xsl:value-of select="$i18n.SortIndex"/>
				</strong>
				
				<xsl:variable name="sortIndexFieldname" select="concat(id,'-sortIndex')"/>
				
				<xsl:apply-templates select="../../ValidationErrors/validationError[fieldName = $sortIndexFieldname]"/>
				
				<div class="full">
					<xsl:call-template name="createDropdown">
						<xsl:with-param name="name" select="$sortIndexFieldname"/>
						<xsl:with-param name="element" select="$sortIndexOptions"/>
						<xsl:with-param name="labelElementName" select="'name'" />
						<xsl:with-param name="valueElementName" select="'value'" />
						<xsl:with-param name="selectedValue" select="$providerConfiguration/sortIndex" />
						<xsl:with-param name="requestparameters" select="../../requestparameters"/>
					</xsl:call-template>				
				</div>				
			</div>		
		
		</div>
		
	</div>
	
	<xsl:call-template name="initializeFCKEditor">
		<xsl:with-param name="basePath"><xsl:value-of select="/Document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/>/ckeditor/</xsl:with-param>
		<xsl:with-param name="customConfig">config.js</xsl:with-param>
		<xsl:with-param name="editorContainerClass">htmleditor</xsl:with-param>
		<xsl:with-param name="editorHeight">200</xsl:with-param>
		<xsl:with-param name="contentsCss">
			<xsl:if test="/Document/cssPath">
				<xsl:value-of select="/Document/cssPath"/>
			</xsl:if>
		</xsl:with-param>
	</xsl:call-template>	
	
	</xsl:template>
	
	<xsl:template match="validationError">
		<xsl:if test="fieldName and validationErrorType and not(messageKey)">
			<p class="error">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:value-of select="$i18n.validationError.requiredField" />
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:value-of select="$i18n.validationError.invalidFormat" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooShort'">
						<xsl:value-of select="$i18n.validationError.tooShort" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooLong'">
						<xsl:value-of select="$i18n.validationError.tooLong" />
					</xsl:when>		
					<xsl:otherwise>
						<xsl:value-of select="$i18n.validationError.unknownError" />
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>!</xsl:text>
			</p>
		</xsl:if>
		
	</xsl:template>	
	
</xsl:stylesheet>