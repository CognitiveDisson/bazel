    List<String> newContent = new ArrayList<>(oldContent);
    // Apply the chunks separately and in reverse order to workaround an issue in applyFuzzy.
    // See https://github.com/java-diff-utils/java-diff-utils/pull/125#issuecomment-1749385825
    for (AbstractDelta<String> delta : Lists.reverse(patch.getDeltas())) {
      Patch<String> tmpPatch = new Patch<>();
      tmpPatch.addDelta(delta);
      try {
        newContent = tmpPatch.applyFuzzy(newContent, 0);
      } catch (PatchFailedException | IndexOutOfBoundsException e) {
        throw new PatchFailedException(
            String.format(
                "in patch applied to %s: %s, error applying change near line %s",
                oldFile, e.getMessage(), delta.getSource().getPosition() + 1));
      }
    }