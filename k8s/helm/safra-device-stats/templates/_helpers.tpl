{{/*
Expand the name of the chart.
*/}}
{{- define "safra-device-stats.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "safra-device-stats.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "safra-device-stats.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "safra-device-stats.labels" -}}
helm.sh/chart: {{ include "safra-device-stats.chart" . }}
{{ include "safra-device-stats.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "safra-device-stats.selectorLabels" -}}
app.kubernetes.io/name: {{ include "safra-device-stats.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "safra-device-stats.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "safra-device-stats.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Statistics API labels
*/}}
{{- define "safra-device-stats.statisticsApi.labels" -}}
{{ include "safra-device-stats.labels" . }}
app.kubernetes.io/component: statistics-api
tier: frontend
{{- end }}

{{/*
Statistics API selector labels
*/}}
{{- define "safra-device-stats.statisticsApi.selectorLabels" -}}
{{ include "safra-device-stats.selectorLabels" . }}
app.kubernetes.io/component: statistics-api
tier: frontend
{{- end }}

{{/*
Device Registration API labels
*/}}
{{- define "safra-device-stats.deviceRegistrationApi.labels" -}}
{{ include "safra-device-stats.labels" . }}
app.kubernetes.io/component: device-registration-api
tier: backend
{{- end }}

{{/*
Device Registration API selector labels
*/}}
{{- define "safra-device-stats.deviceRegistrationApi.selectorLabels" -}}
{{ include "safra-device-stats.selectorLabels" . }}
app.kubernetes.io/component: device-registration-api
tier: backend
{{- end }}