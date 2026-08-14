export interface ChartPoint{

    time:string

    value:number

}


export interface SensorChartData{

    sensorName:string

    unit:string

    points:ChartPoint[]

}