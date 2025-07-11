export type StudentData = {
    id: number,
    conceptUnderstanding: number,
    logicalReasoning: number,
    problemSolving: number,
    innovativeThinking: number,
    expressionNorms: number
}

export type StudentDataCreateDTO = Omit<StudentData, 'id'> & {

}

export type StudentDataUpdateDTO = Omit<StudentDataCreateDTO, 'id'> & {
    id: number
}