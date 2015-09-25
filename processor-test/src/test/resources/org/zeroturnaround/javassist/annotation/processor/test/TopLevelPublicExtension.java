package org.zeroturnaround.javassist.annotation.processor.test;

import org.zeroturnaround.javassist.annotation.Patches;

@Patches(TopLevelPublic.class)
public class TopLevelPublicExtension extends TopLevelPublic_Mirror {

}
