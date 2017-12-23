package com.dnion.app.android.injuriesapp;

import android.util.Pair;

import com.dnion.app.android.injuriesapp.ItemObj;
import com.dnion.app.android.injuriesapp.ItemType;
import com.dnion.app.android.injuriesapp.dao.PatientInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 卫华 on 2017/5/1.
 */

public class ArchivesData {

    public static List<Pair> typeDict =  new ArrayList<Pair>();
    public static List<Pair> measuresDict =  new ArrayList<Pair>();
    public static List<Pair> ctaDict =  new ArrayList<Pair>();
    public static List<Pair> woundDressingType = new ArrayList<Pair>();
    public static List<Pair> woundSkinDict = new ArrayList<Pair>();

    public static List<Pair> typeEnDict =  new ArrayList<Pair>();
    public static List<Pair> measuresEnDict =  new ArrayList<Pair>();
    public static List<Pair> ctaEnDict =  new ArrayList<Pair>();
    public static List<Pair> woundSkinEnDict = new ArrayList<Pair>();
    public static List<Pair> woundDressingTypeEn = new ArrayList<Pair>();

    public static Map<Integer, String> woundDressingDisc = new HashMap<Integer, String>();
    public static Map<Integer, String> woundDressingEnDisc = new HashMap<Integer, String>();

    static {
        typeDict.add(Pair.create(1, "外科伤口"));
        typeDict.add(Pair.create(2, "压疮"));
        typeDict.add(Pair.create(3, "糖尿病溃疡"));
        typeDict.add(Pair.create(4, "静脉性溃疡"));
        typeDict.add(Pair.create(5, "动脉性溃疡"));
        typeDict.add(Pair.create(6, "癌性伤口"));
        typeDict.add(Pair.create(7, "放射性损伤"));
        typeDict.add(Pair.create(8, "瘘管"));
        typeDict.add(Pair.create(0, "其他"));

        typeEnDict.add(Pair.create(1, "Surgical Wound"));
        typeEnDict.add(Pair.create(2, "Pressure Sore"));
        typeEnDict.add(Pair.create(3, "Diabetes Ulcers"));
        typeEnDict.add(Pair.create(4, "Venous Ulcer"));
        typeEnDict.add(Pair.create(5, "Arterial Ulcer"));
        typeEnDict.add(Pair.create(6, "Cancerous Wound"));
        typeEnDict.add(Pair.create(7, "Radiation Damage"));
        typeEnDict.add(Pair.create(8, "Fistula"));
        typeEnDict.add(Pair.create(0, "Other"));

        measuresDict.add(Pair.create(1, "社区换药"));
        measuresDict.add(Pair.create(2, "门诊换药"));
        measuresDict.add(Pair.create(3, "住院治疗（内科）"));
        measuresDict.add(Pair.create(4, "住院治疗（外科）"));

        measuresEnDict.add(Pair.create(1, "Community treatment"));
        measuresEnDict.add(Pair.create(2, "Outpatient treatment"));
        measuresEnDict.add(Pair.create(3, "Hospitalization (internal medicine)"));
        measuresEnDict.add(Pair.create(4, "Hospitalization (surgery)"));

        ctaDict.add(Pair.create(1, "下肢生干血管"));
        ctaDict.add(Pair.create(2, "腘动脉"));
        ctaDict.add(Pair.create(3, "胫后动脉"));
        ctaDict.add(Pair.create(4, "胫前动脉"));
        ctaDict.add(Pair.create(5, "腓浅动脉"));
        ctaDict.add(Pair.create(6, "下肢生干血管"));

        ctaEnDict.add(Pair.create(1, "Lower Extremities Stem Blood Vessels"));
        ctaEnDict.add(Pair.create(2, "Popliteal Artery"));
        ctaEnDict.add(Pair.create(3, "Posterior tibial Artery"));
        ctaEnDict.add(Pair.create(4, "Anterior tibial Artery"));
        ctaEnDict.add(Pair.create(5, "Superficial Peroneal Artery"));
        ctaEnDict.add(Pair.create(6, "Lower Extremities Stem Blood Vessels"));

        woundDressingType.add(Pair.create(1, "薄膜类敷料"));
        woundDressingType.add(Pair.create(2, "水凝胶敷料"));
        woundDressingType.add(Pair.create(3, "水胶体敷料"));
        woundDressingType.add(Pair.create(4, "泡沫敷料"));
        woundDressingType.add(Pair.create(5, "新型藻酸盐类敷料"));
        woundDressingType.add(Pair.create(6, "药用类敷料"));
        woundDressingType.add(Pair.create(7, "其他"));

        woundDressingTypeEn.add(Pair.create(1, "Film Dressings"));
        woundDressingTypeEn.add(Pair.create(2, "Hydrogel Dressings"));
        woundDressingTypeEn.add(Pair.create(3, "Hydrocolloid Dressings"));
        woundDressingTypeEn.add(Pair.create(4, "Foam Dressings"));
        woundDressingTypeEn.add(Pair.create(5, "New Alginate Dressing"));
        woundDressingTypeEn.add(Pair.create(6, "Medicinal Dressings"));
        woundDressingTypeEn.add(Pair.create(7, "Other"));

        woundSkinDict.add(Pair.create(7, "完好无损"));
        woundSkinDict.add(Pair.create(1, "苍白"));
        woundSkinDict.add(Pair.create(2, "红斑"));
        woundSkinDict.add(Pair.create(3, "浸渍"));
        woundSkinDict.add(Pair.create(4, "色素沉着"));
        woundSkinDict.add(Pair.create(5, "水肿"));
        woundSkinDict.add(Pair.create(6, "坏死"));
        woundSkinDict.add(Pair.create(8, "青灰色"));
        woundSkinDict.add(Pair.create(9, "干燥起皮"));
        woundSkinDict.add(Pair.create(10, "硬化结茧"));
        woundSkinDict.add(Pair.create(11, "角化过度"));

        woundSkinEnDict.add(Pair.create(7, "Intact"));
        woundSkinEnDict.add(Pair.create(1, "Pale"));
        woundSkinEnDict.add(Pair.create(2, "Erythema"));
        woundSkinEnDict.add(Pair.create(3, "Impregnation"));
        woundSkinEnDict.add(Pair.create(4, "Hyperpigmentation"));
        woundSkinEnDict.add(Pair.create(5, "Edema"));
        woundSkinEnDict.add(Pair.create(6, "Thanatosis"));
        woundSkinEnDict.add(Pair.create(8, "Cinerous"));
        woundSkinEnDict.add(Pair.create(9, "Dry Peeling"));
        woundSkinEnDict.add(Pair.create(10, "Hardening of the Cocoon"));
        woundSkinEnDict.add(Pair.create(11, "Hyperkeratosis"));

        woundDressingDisc.put(1, "主要由聚氨酯类材料和脱敏医用粘胶组成，分内外两层，内层为未水性材料，可吸收创面渗液，外层材料具有良好的透气性和弹性。" +
                "此类敷料的特点是透明，便于观察伤口，能密切黏附于创面表面，有效保持创面渗出液，从而提供有利于创面愈合的湿润环境，促使坏死组织脱落；" +
                "缺点为该类敷料吸水性能欠佳，吸收饱和后易致膜下渗液积聚，可能诱发或加重感染，故只适用于相对清洁创面，不适于渗液多的创面。" +
                "有临床研究表明[1]，在气管切开护理及中心静脉置管的维护中应用该类敷料的疗效较好，不但能有效防止感染，同时还能改善患者的舒适，提高生质量值得临床推广应用。");

        woundDressingDisc.put(2, "是将水溶性高分子材料或其单体经特殊加工形成的一种具有三维网状结构且不溶于水的胶状物质，主要成分为纯水70%-90%、羧甲基纤维索及其他一些附加成分。" +
                "有临床报道指出[2]：水凝胶敷料能与不平整的创面紧密黏合，减少细菌滋生的机会，防止创面感染，加速新生血管生成，促进上皮细胞生长。水凝胶的主要作用为自体清创，机制是在湿润环境中依靠创面自身渗出液中的胶原蛋白降解酶分解坏死物质。");

        woundDressingDisc.put(3, "水胶体类敷料是由亲水胶微粒的明胶、果胶和羧甲基纤维素混合形成。主要适应于Ⅰ、Ⅱ期压疮的预防与治疗，烧伤、整形供皮区的治疗[3]，各类浅表外伤口和整形美容伤口的治疗，慢性伤口上皮形成期及静脉炎的预防与治疗等[4]。");
        woundDressingDisc.put(4, "新型的泡沫类敷料外层为疏水材料，内层为亲水材料。此类敷料具有多孔性，表面张力低，富有弹性，可塑性强、轻便，对渗出液的吸收力可达到敷料本身质量的10倍。" +
                "泡沫性敷料对创面渗出物的处理是靠水蒸汽的转运和吸收机制来控制的，可塑性好，能制成各种厚度，对创面具有较好的保护作用。" +
                "目前使用最多的材料是聚氨酯泡沫和聚乙烯醇泡沫。其缺点是粘贴性较差，而需外固定材料；敷料不透明，难以观察创面情况；敷料孔隙大，创面肉芽组织易于长入，造成脱膜困难， 而且易受细菌污染[5]。");
        woundDressingDisc.put(5, "主要成分取自海水中的藻类，它是利用藻类中类似纤维素的不能溶解的多糖藻酸盐制成的敷料。海藻酸可与金属离子结合形成盐，是各种金属离子的良好载体，常见的有藻酸钙盐、藻酸锌盐敷料。" +
                "藻酸钙盐敷料在更换过程中不造成新鲜肉芽损伤引起的疼痛，易被患者接受，还可以通过钙、钠离子交换，达到止血功能，同时还具有吸附细菌，阻止细菌进入创面的功能[6]。研究显示，藻酸锌盐敷料具有很好的凝血效应和增强血小板活性的作用。[7]");
        woundDressingDisc.put(6, "即用浸渍或涂敷方法将药物涂覆于敷料上，如软膏类敷料、消毒敷料，以及中药敷料等，有保护创面、止痛、止血、消炎、促进新生肉芽组织及上皮细胞生长，加速创面愈合等功能。例如：具有抗菌作用的磺胺嘧啶银敷料、二氧化钛抗菌敷料，具有消炎作用的利多卡因敷料，具有快速祛痛、止血消炎功能的中草药敷料。");
        woundDressingDisc.put(7, "其他");

        woundDressingEnDisc.put(1, "It is mainly composed of polyurethane and desensitizing medical adhesive. It consists of two layers, the inner layer is non water material, " +
                "it can absorb wound exudate, and the outer layer material has good permeability and elasticity. The characteristics of this kind of dressing is transparent, " +
                "easy to observe the wound, closely adhering to the wound surface, effectively maintain the wound exudate, which is conducive to wound healing in moist environment, " +
                "promoting necrotic cell shedding; disadvantages for this kind of dressing is poor water absorption, " +
                "absorption saturation caused by infiltration of liquid film easy to accumulate, may induce or aggravate infection, therefore, " +
                "it is only suitable for the surface which  is relatively clean, not suitable for the liquid infiltration of the wound. " +
                "Clinical studies have shown that in the center of the tracheotomy care and maintenance of venipuncture in application of this kind of dressing curative effect " +
                "is good, not only can effectively prevent infection, also can improve the patient's comfort.");

        woundDressingEnDisc.put(2, "It is a gelatinous material with three dimensional network structure and water insoluble, " +
                "which is formed by special processing of water-soluble polymer materials or monomers. Its main components are pure water 70%-90%, " +
                "carboxymethyl fiber cable and other additional components. According to clinical reports, [2]: hydrogel dressing can closely bond with uneven wounds," +
                " reduce the chances of bacterial growth, prevent wound infection, accelerate angiogenesis and promote epithelial cell growth. " +
                "The main function of the hydrogel is self debridement. The mechanism is to decompose the necrotic substance by the collagen degrading enzyme " +
                "in the wound self exudation fluid in the moist environment.");

        woundDressingEnDisc.put(3, "Hydrocolloid dressings are formed by the mixture of gelatin, pectin and carboxymethyl cellulose, " +
                "which are hydrophilic particles. It is mainly used for prevention and treatment of stage I and II pressure sores, " +
                "treatment of burn, plastic skin donor site, [3], treatment of various superficial wounds and cosmetic wounds, " +
                "prevention and treatment of chronic wound epithelialization and phlebitis, [4].");

        woundDressingEnDisc.put(4, "The outer layer of a new type of foam dressing is a hydrophobic material, and the inner layer is hydrophilic. " +
                "Such dressings are porous, low surface tension, elastic, strong plasticity and light, " +
                "and the absorbability of the exudate can reach 10 times the quality of the dressings. " +
                "The treatment of wound exudates by foam dressing is controlled by the mechanism of water vapor transport and absorption. It has good plasticity, " +
                "can be made into various thickness, and has good protective effect on wounds. The most used materials are polyurethane foam and polyvinyl alcohol foam." +
                " The drawback is that it is poor in stickability and needs external fixator. Dressing is opaque. It is difficult to observe the wound condition. " +
                "It has large pore size and easy to grow into granulation tissue, resulting in difficulty of membrane removal and bacterial contamination. [5]");

        woundDressingEnDisc.put(5, "The main ingredient is the algae in the sea, which is used to make use of the insoluble polysaccharide alginate of algae like cellulose in algae. " +
                "Alginate can combine with metal ions to form salt, which is a good carrier of various metal ions, " +
                "and common alginate and calcium alginate dressing. Alginate calcium dressing does not cause pain caused by fresh granulation injury during replacement. " +
                "It is easy to be accepted by patients, and can also be used to stop bleeding by the exchange of calcium and sodium ion. " +
                "It also has the function of absorbing bacteria and preventing bacteria from entering the wound. " +
                "Studies have shown that zinc alginate dressing has a good effect on coagulation and enhanced platelet activity.anced platelet activity.");

        woundDressingEnDisc.put(6, "It uses impregnation or coating methods to coat drugs on dressings, such as ointment dressing, " +
                "disinfectant dressings and traditional Chinese dressing. It has functions of protecting wounds, " +
                "relieving pain, stopping bleeding, diminishing inflammation, promoting granulation tissue and epithelial cells growth and accelerating wound healing. " +
                "For example, there are antibacterial silver sulfadiazine dressing, titanium dioxide antibacterial dressing, anti-inflammatory lidocaine dressing, " +
                "and a Chinese herbal dressing with fast dispelling pain, hemostasis and anti-inflammatory function.");
        woundDressingEnDisc.put(7, "Other");


    }

}
