import { Controller } from '../lib/Controller';


export class FooterPopUpController extends Controller {
    static selector: string = '.dialog';

    constructor(element: HTMLElement) {
        super(element);
        element.addEventListener('click', function () {
            let textDiv = document.getElementById("textDiv");
            if (textDiv.style.display == 'none') {
                textDiv.style.display = 'block';
                popSwitch(element.getAttribute("value"));
                
            }
            else {
                textDiv.style.display = 'none';
            }
        });

        document.getElementById('close').addEventListener('click',closeButton);
        document.addEventListener('click',clickOutside);
    }
}

function clickOutside(e: Event) {
    e = e || window.event;
    let target = e.srcElement;
        if (<string>target.className != "popUpText" && target.className != "dialog") {
            document.getElementById("textDiv") != null ? document.getElementById("textDiv").style.display = "none" : document.removeEventListener("clickoutside",this);
        }
        else if (target.className == "dialog") {
            let textDiv = document.getElementById("textDiv");
            if (textDiv.style.display == 'none') {
                textDiv.style.display = 'block';
                popSwitch(target.getAttribute("value"));    
        }
    }
}

function popSwitch(value:string){
    switch (value) {
        case "about": popAbout(); break;
        case "privacy": popPrivacy(); break;
        case "terms": popTerms(); break;
        case "contact": popContact(); break;
        default: break;
    }
}

function closeButton() {
    document.getElementById("textDiv").style.display = "none";
}

function popAbout() {
    document.getElementById("textSpan").innerHTML = `
    <h2 style="text-align: center;">About Ennui</h2>
    <br>
    <h3 style="text-align: center;">Ennui is a Web Application for finding the best “time killer” for each situation</h3>

    <br>
    <div style="margin-right: 100;">
        <ul>
            <li><b>Events</b>
                <ul>
                    <li>Limited time-range; e.g “Oktoberfest”, “Pflasterspektakel”, “Flohmarkt”</li>
                    <li>fetched from Facebook API or added by users</li>
                </ul>
            </li>
            <li><b>Offers</b>
                <ul>
                    <li>No time Limit; e.g “Bowling”, “Climbing”, “Laser Tag”)</li>
                    <li>fetched from Google Maps API</li>
                </ul>
            </li>
            <li><b>Games</b>
                <ul>
                    <li>Doesn’t require any host/specific location; e.g “Schnopsn”, “Merkball”, “Busfoahn”</li>
                    <li> added by users</li>
                </ul>
            </li>
        </ul>
    </div>
    `;
}
function popPrivacy() {
    document.getElementById("textSpan").innerHTML = `<style>
#ppBody
{
    font-size:11pt;
    width:100%;
    margin:0 auto;
    display:block;
    text-align:justify;
}

#ppHeader
{
    font-family:verdana;
    font-size:21pt;
    width:100%;
    margin:0 auto;
}

.ppConsistencies
{
    display:none;
}
</style><div id='ppHeader'>ennui.at Privacy Policy</div><div id='ppBody'><div class='ppConsistencies'><div class='col-2'>
            <div class="quick-links text-center">Information Collection</div>
        </div><div class='col-2'>
            <div class="quick-links text-center">Information Usage</div>
        </div><div class='col-2'>
            <div class="quick-links text-center">Information Protection</div>
        </div><div class='col-2'>
            <div class="quick-links text-center">Cookie Usage</div>
        </div><div class='col-2'>
            <div class="quick-links text-center">3rd Party Disclosure</div>
        </div><div class='col-2'>
            <div class="quick-links text-center">3rd Party Links</div>
        </div><div class='col-2'></div></div><div style='clear:both;height:10px;'></div><div class='ppConsistencies'><div class='col-2'>
            <div class="col-12 quick-links2 gen-text-center">Google AdSense</div>
        </div><div class='col-2'>
            <div class="col-12 quick-links2 gen-text-center">
                    Fair Information Practices
                    <div class="col-8 gen-text-left gen-xs-text-center" style="font-size:12px;position:relative;left:20px;">Fair information<br> Practices</div>
                </div>
        </div><div class='col-2'>
            <div class="col-12 quick-links2 gen-text-center coppa-pad">
                    COPPA

                </div>
        </div><div class='col-2'>
            <div class="col-12 quick-links2 quick4 gen-text-center caloppa-pad">
                    CalOPPA

                </div>
        </div><div class='col-2'>
            <div class="quick-links2 gen-text-center">Our Contact Information<br></div>
        </div></div><div style='clear:both;height:10px;'></div>
<div class='innerText'>This privacy policy has been compiled to better serve those who are concerned with how their 'Personally Identifiable Information' (PII) is being used online. PII, as described in US privacy law and information security, is information that can be used on its own or with other information to identify, contact, or locate a single person, or to identify an individual in context. Please read our privacy policy carefully to get a clear understanding of how we collect, use, protect or otherwise handle your Personally Identifiable Information in accordance with our website.<br></div><span id='infoCo'></span><br><div class='grayText'><strong>What personal information do we collect from the people that visit our blog, website or app?</strong></div><br /><div class='innerText'>When ordering or registering on our site, as appropriate, you may be asked to enter your name, email address  or other details to help you with your experience.</div><br><div class='grayText'><strong>When do we collect information?</strong></div><br /><div class='innerText'>We collect information from you when you register on our site or enter information on our site.</div><br> <span id='infoUs'></span><br><div class='grayText'><strong>How do we use your information? </strong></div><br /><div class='innerText'> We may use the information we collect from you when you register, make a purchase, sign up for our newsletter, respond to a survey or marketing communication, surf the website, or use certain other site features in the following ways:<br><br></div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> To personalize your experience and to allow us to deliver the type of content and product offerings in which you are most interested.</div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> To improve our website in order to better serve you.</div><span id='infoPro'></span><br><div class='grayText'><strong>How do we protect your information?</strong></div><br /><div class='innerText'>We do not use vulnerability scanning and/or scanning to PCI standards.</div><div class='innerText'>We only provide articles and information. We never ask for credit card numbers.</div><div class='innerText'>We do not use Malware Scanning.<br><br></div><div class='innerText'>We do not use an SSL certificate</div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> We only provide articles and information. We never ask for personal or private information like names, email addresses, or credit card numbers.</div><span id='coUs'></span><br><div class='grayText'><strong>Do we use 'cookies'?</strong></div><br /><div class='innerText'>Yes. Cookies are small files that a site or its service provider transfers to your computer's hard drive through your Web browser (if you allow) that enables the site's or service provider's systems to recognize your browser and capture and remember certain information. For instance, we use cookies to help us remember and process the items in your shopping cart. They are also used to help us understand your preferences based on previous or current site activity, which enables us to provide you with improved services. We also use cookies to help us compile aggregate data about site traffic and site interaction so that we can offer better site experiences and tools in the future.</div><div class='innerText'><br><strong>We use cookies to:</strong></div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Understand and save user's preferences for future visits.</div><div class='innerText'><br>You can choose to have your computer warn you each time a cookie is being sent, or you can choose to turn off all cookies. You do this through your browser settings. Since browser is a little different, look at your browser's Help Menu to learn the correct way to modify your cookies.<br></div><br><div class='innerText'>If you turn cookies off, Some of the features that make your site experience more efficient may not function properly.It won't affect the user's experience that make your site experience more efficient and may not function properly.</div><br><span id='trDi'></span><br><div class='grayText'><strong>Third-party disclosure</strong></div><br /><div class='innerText'>We do not sell, trade, or otherwise transfer to outside parties your Personally Identifiable Information.</div><span id='trLi'></span><br><div class='grayText'><strong>Third-party links</strong></div><br /><div class='innerText'>We do not include or offer third-party products or services on our website.</div><span id='gooAd'></span><br><div class='blueText'><strong>Google</strong></div><br /><div class='innerText'>Google's advertising requirements can be summed up by Google's Advertising Principles. They are put in place to provide a positive experience for users. https://support.google.com/adwordspolicy/answer/1316548?hl=en <br><br></div><div class='innerText'>We have not enabled Google AdSense on our site but we may do so in the future.</div><span id='calOppa'></span><br><div class='blueText'><strong>California Online Privacy Protection Act</strong></div><br /><div class='innerText'>CalOPPA is the first state law in the nation to require commercial websites and online services to post a privacy policy.  The law's reach stretches well beyond California to require any person or company in the United States (and conceivably the world) that operates websites collecting Personally Identifiable Information from California consumers to post a conspicuous privacy policy on its website stating exactly the information being collected and those individuals or companies with whom it is being shared. -  See more at: http://consumercal.org/california-online-privacy-protection-act-caloppa/#sthash.0FdRbT51.dpuf<br></div><div class='innerText'><br><strong>According to CalOPPA, we agree to the following:</strong><br></div><div class='innerText'>Users can visit our site anonymously.</div><div class='innerText'>Once this privacy policy is created, we will add a link to it on our home page or as a minimum, on the first significant page after entering our website.<br></div><div class='innerText'>Our Privacy Policy link includes the word 'Privacy' and can easily be found on the page specified above.</div><div class='innerText'><br>You will be notified of any Privacy Policy changes:</div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> On our Privacy Policy Page<br></div><div class='innerText'>Can change your personal information:</div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> By logging in to your account</div><div class='innerText'><br><strong>How does our site handle Do Not Track signals?</strong><br></div><div class='innerText'>We don't honor Do Not Track signals and Do Not Track, plant cookies, or use advertising when a Do Not Track (DNT) browser mechanism is in place. We don't honor them because:<br></div><div class='innerText'>Our website needs the location of the user to work</div><div class='innerText'><br><strong>Does our site allow third-party behavioral tracking?</strong><br></div><div class='innerText'>It's also important to note that we do not allow third-party behavioral tracking</div><span id='coppAct'></span><br><div class='blueText'><strong>COPPA (Children Online Privacy Protection Act)</strong></div><br /><div class='innerText'>When it comes to the collection of personal information from children under the age of 13 years old, the Children's Online Privacy Protection Act (COPPA) puts parents in control.  The Federal Trade Commission, United States' consumer protection agency, enforces the COPPA Rule, which spells out what operators of websites and online services must do to protect children's privacy and safety online.<br><br></div><div class='innerText'>We do not specifically market to children under the age of 13 years old.</div><div class='innerText'>Do we let third-parties, including ad networks or plug-ins collect PII from children under 13?</div><span id='ftcFip'></span><br><div class='blueText'><strong>Fair Information Practices</strong></div><br /><div class='innerText'>The Fair Information Practices Principles form the backbone of privacy law in the United States and the concepts they include have played a significant role in the development of data protection laws around the globe. Understanding the Fair Information Practice Principles and how they should be implemented is critical to comply with the various privacy laws that protect personal information.<br><br></div><div class='innerText'><strong>In order to be in line with Fair Information Practices we will take the following responsive action, should a data breach occur:</strong></div><div class='innerText'>We will notify the users via in-site notification</div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Within 1 business day</div><div class='innerText'><br>We also agree to the Individual Redress Principle which requires that individuals have the right to legally pursue enforceable rights against data collectors and processors who fail to adhere to the law. This principle requires not only that individuals have enforceable rights against data users, but also that individuals have recourse to courts or government agencies to investigate and/or prosecute non-compliance by data processors.</div><span id='canSpam'></span><br><div class='blueText'><strong>CAN SPAM Act</strong></div><br /><div class='innerText'>The CAN-SPAM Act is a law that sets the rules for commercial email, establishes requirements for commercial messages, gives recipients the right to have emails stopped from being sent to them, and spells out tough penalties for violations.<br><br></div><div class='innerText'><strong>We collect your email address in order to:</strong></div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Send information, respond to inquiries, and/or other requests or questions</div><div class='innerText'><br><strong>To be in accordance with CANSPAM, we agree to the following:</strong></div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Not use false or misleading subjects or email addresses.</div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Identify the message as an advertisement in some reasonable way.</div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Include the physical address of our business or site headquarters.</div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Monitor third-party email marketing services for compliance, if one is used.</div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Honor opt-out/unsubscribe requests quickly.</div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Allow users to unsubscribe by using the link at the bottom of each email.</div><div class='innerText'><strong><br>If at any time you would like to unsubscribe from receiving future emails, you can email us at</strong></div><div class='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Follow the instructions at the bottom of each email.</div> and we will promptly remove you from <strong>ALL</strong> correspondence.</div><br><span id='ourCon'></span><br><div class='blueText'><strong>Contacting Us</strong></div><br /><div class='innerText'>If there are any questions regarding this privacy policy, you may contact us using the information below.<br><br></div><div class='innerText'>ennui.at</div><div class='innerText'>Limesstraße 12-14</div>Leonding, Oberösterreich 4060 <div class='innerText'>Austria</div><div class='innerText'>crawler.wastun@gmail.com</div><div class='innerText'><br>Last Edited on 2017-09-25</div></div>`;
}
function popTerms() {
    document.getElementById("textSpan").innerHTML = `<h2 style="text-align: center;">TERMS AND CONDITIONS</h2><ol><li><strong>Introduction</strong></li></ol><p>These Website Standard Terms and Conditions written on this webpage shall manage your use of this website. These Terms will be applied fully and affect to your use of this Website. By using this Website, you agreed to accept all terms and conditions written in here. You must not use this Website if you disagree with any of these Website Standard Terms and Conditions.</p><p>Minors or people below 18 years old are not allowed to use this Website.</p><ol start="2"><li><strong>Intellectual Property Rights</strong></li></ol><p>Other than the content you own, under these Terms, ennui AT and/or its licensors own all the intellectual property rights and materials contained in this Website.</p><p>You are granted limited license only for purposes of viewing the material contained on this Website.</p><ol start="3"><li><strong>Restrictions</strong></li></ol><p>You are specifically restricted from all of the following</p><ul><li>publishing any Website material in any other media;</li><li>selling, sublicensing and/or otherwise commercializing any Website material;</li><li>publicly performing and/or showing any Website material;</li><li>using this Website in any way that is or may be damaging to this Website;</li><li>using this Website in any way that impacts user access to this Website;</li><li>using this Website contrary to applicable laws and regulations, or in any way may cause harm to the Website, or to any person or business entity;</li><li>engaging in any data mining, data harvesting, data extracting or any other similar activity in relation to this Website;</li><li>using this Website to engage in any advertising or marketing.</li></ul><p>Certain areas of this Website are restricted from being access by you and ennui AT may further restrict access by you to any areas of this Website, at any time, in absolute discretion. Any user ID and password you may have for this Website are confidential and you must maintain confidentiality as well.</p><ol start="4"><li><strong>Your Content</strong></li></ol><p>In these Website Standard Terms and Conditions, “Your Content” shall mean any audio, video text, images or other material you choose to display on this Website. By displaying Your Content, you grant ennui AT a non-exclusive, worldwide irrevocable, sub licensable license to use, reproduce, adapt, publish, translate and distribute it in any and all media.</p><p>Your Content must be your own and must not be invading any third-party’s rights. ennui AT reserves the right to remove any of Your Content from this Website at any time without notice.</p><ol start="5"><li><strong>No warranties</strong></li></ol><p>This Website is provided “as is,” with all faults, and ennui AT express no representations or warranties, of any kind related to this Website or the materials contained on this Website. Also, nothing contained on this Website shall be interpreted as advising you.</p><ol start="6"><li><strong>Limitation of liability</strong></li></ol><p>In no event shall ennui AT, nor any of its officers, directors and employees, shall be held liable for anything arising out of or in any way connected with your use of this Website whether such liability is under contract. &nbsp;ennui AT, including its officers, directors and employees shall not be held liable for any indirect, consequential or special liability arising out of or in any way related to your use of this Website.</p><ol start="7"><li><strong>Indemnification</strong></li></ol><p>You hereby indemnify to the fullest extent ennui AT from and against any and/or all liabilities, costs, demands, causes of action, damages and expenses arising in any way related to your breach of any of the provisions of these Terms.</p><ol start="8"><li><strong>Severability</strong></li></ol><p>If any provision of these Terms is found to be invalid under any applicable law, such provisions shall be deleted without affecting the remaining provisions herein.</p><ol start="9"><li><strong>Variation of Terms</strong></li></ol><p>ennui AT is permitted to revise these Terms at any time as it sees fit, and by using this Website you are expected to review these Terms on a regular basis.</p><ol start="10"><li><strong>Assignment</strong></li></ol><p>The ennui AT is allowed to assign, transfer, and subcontract its rights and/or obligations under these Terms without any notification. However, you are not allowed to assign, transfer, or subcontract any of your rights and/or obligations under these Terms.</p><ol start="11"><li><strong>Entire Agreement</strong></li></ol><p>These Terms constitute the entire agreement between ennui AT and you in relation to your use of this Website, and supersede all prior agreements and understandings.</p><ol start="12"><li><strong>Governing Law &amp; Jurisdiction</strong></li></ol><p>These Terms will be governed by and interpreted in accordance with the laws of the State of Austria, and you submit to the non-exclusive jurisdiction of the state and federal courts located in Austria for the resolution of any disputes.</p><p>These terms and conditions have been generated at <a href="https://termsandcondiitionssample.com/" target="_blank">termsandcondiitionssample.com</a>.</p><p><a href="http://ultimatewebtraffic.com/" target="_blank">visit us</a></p>`;
}
function popContact() {
   document.getElementById("textSpan").innerHTML = `
    <h1 style="text-align: center;">Contact us</h1>
    <br>
    <h2 style="text-align: center;">If you have any questions for the ennui.at-Developers team feel free to contact us:</h2>
    <br>
    <div style="text-align: center; font-size: medium;">
       <b><p>ennui AT</p>
        <p>Limesstraße 12-14</p>
        <p>Tel: +43650 9579101</p>
        <p>E-Mail: crawler.wastun@gmail.com</p>
       </b>
    </div>
    `;
}