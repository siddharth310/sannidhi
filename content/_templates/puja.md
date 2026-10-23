---
id: <deity>.puja.<slug>
deity: <deity>
kind: festival                    # festival | vratam | nitya
observance: obs.<slug>            # omit for nitya
title: { te: <>, hi: <>, en: <> }
minutes: <total>
regions: [all]                    # or state codes
status: draft
reviewed_by:
---
## pre

### <slug> | <IAST title>
image: img.puja.<deity>.<slug>    # optional
minutes: <n>                      # optional
#### te
<what to do>
#### hi
<>
#### en
<>

### samagri | Sāmagrī
| item | qty | required | alternatives |
|---|---|---|---|
| sm.<id> | <qty> | yes | sm.<id> |

## during

### <slug> | <IAST title>
mantra: <deity>.mantra.<slug>     # one id or a list
repeat: <n>                       # optional
text: <deity>.<type>              # optional: a nāmalu to chant here
fills: [name, gotra]              # optional: saṅkalpam fields
optional: yes                     # optional
#### te
<>
#### hi
<>
#### en
<>

## post

### naivedyam | Naivedyam
naivedyam: [nv.<slug>]
#### te
<>
#### hi
<>
#### en
<>
