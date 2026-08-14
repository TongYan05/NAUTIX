/* TypeScript type definitions corresponding to shipsensor backend entities */

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages?: number
}

export interface ShipInfo {
  id?: number
  shipName?: string
  imo?: string
  registryPort?: string
  shipType?: string
  buildYear?: number
  lengthOverall?: number
  beam?: number
  draft?: number
  displacement?: number
  mainEngineModel?: string
  sailingSpeed?: number
  shipyardBuilder?: string
  operatingCompany?: string
  surveyValidDate?: string
  createTime?: string
  updateTime?: string
}

export interface SensorConfig {
  id?: number
  shipId?: number
  typeId?: number
  sensorName?: string
  installDate?: string
  status?: number
  createTime?: string
}

export interface SensorDict {
  id?: number
  typeCode?: string
  typeName?: string
  defaultUnit?: string
  description?: string
}

export interface SensorData {
  id?: number
  configId?: number
  shipId?: number
  dataValue?: number
  recordedAt?: string
  createTime?: string
}

export interface AlertRule {
  id?: number
  ruleName?: string
  typeId?: number
  thresholdValue?: number
  operator?: string
  durationSeconds?: number
  alertLevel?: number
}

export interface AlertRecord {
  id?: number
  shipId?: number
  ruleId?: number
  triggerValue?: number
  alertTime?: string
  handleStatus?: number
}

export interface AlertEvent {
  id?: number
  shipId?: number
  ruleId?: number
  startTime?: string
  endTime?: string
  durationSeconds?: number
  maxValue?: number
  status?: number
}

export interface SysOperationLog {
  id?: number
  userName?: string
  operationType?: string
  targetTable?: string
  targetId?: number
  oldValue?: string
  newValue?: string
  createTime?: string
}

export interface Port {
  id?: number
  wpiId?: string
  portName?: string
  country?: string
  countryCode?: string
  portCode?: string
  portType?: string
  latitude?: number
  longitude?: number
  maxShipLength?: number
  maxDraft?: number
}

export interface Route {
  id?: number
  routeName?: string
  startPortId?: number
  endPortId?: number
  distanceNm?: number
  description?: string
}

export interface RoutePoint {
  id?: number
  routeId?: number
  pointOrder?: number
  latitude?: number
  longitude?: number
}

export interface WeatherRegion {
  id?: number
  regionName?: string
  airTemperature?: number
  seaTemperature?: number
  humidity?: number
  pressure?: number
  windSpeed?: number
  windDirection?: number
  waveHeight?: number
  weatherType?: string
  updateTime?: string
}

export interface ShipRuntimeStatus {
  shipId?: number
  sailingStatus?: string
  latitude?: number
  longitude?: number
  speed?: number
  heading?: number
  engineLoad?: number
  fuelPercent?: number
  weatherId?: number
  lastUpdate?: string
}
