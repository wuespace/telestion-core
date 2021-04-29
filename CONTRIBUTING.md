# Contributing Guidelines

## Intended Release Cycle

When you want to release a new application, go to the _Actions_ Tab in the GitHub UI and choose the `Release` Action.
Then click `Run workflow`.

![image](https://user-images.githubusercontent.com/52416718/115860012-71cb7500-a420-11eb-9af1-d5faf0768f67.png)

This triggers the Release Action which automatically creates a Conventional Commit Release on GitHub.
Afterwards the subsequent build actions are triggered via:
```yml
on:
  workflow_run:
    workflows: ["Release"]
    types: [completed]
```

Additionally, if you want to upload release assets in these build workflows, you can use the cached build environment:
```yml
      - name: Download build environment 📥
        uses: dawidd6/action-download-artifact@v2.14.0
        with:
          workflow: ${{ github.event.workflow_run.workflow_id }}
          workflow_conclusion: success
          name: build-env
          path: ${{ github.workspace }}

      - name: Import environment ⛓
        run: cat .build-env >> $GITHUB_ENV

      - name: Upload release asset
        uses: actions/upload-release-asset@v1.0.1
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          upload_url: ${{ env.upload_url }}
          asset_path: ./dist/artifact1.txt
          asset_name: artifact1.txt
          asset_content_type: text/plain
```

## CLA

All contributers are required to sign a contributor's license agreement to contribute to this repository.
For further details, please contact kontakt@wuespace.de. Thank you! :slightly_smiling_face: 
