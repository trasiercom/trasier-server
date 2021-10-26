package com.trasier.server;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.trasier.api.server.model.Span;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

@AutomaticFeature
class RuntimeReflectionRegistrationFeature implements Feature {
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    try {
      RuntimeReflection.register(Span.class.getDeclaredConstructor());
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}