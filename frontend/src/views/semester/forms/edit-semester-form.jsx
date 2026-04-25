/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useState, useEffect } from "react";
import { Form, Input, Modal, Select, Row, Col, message } from "antd";
import { getStudyPrograms } from "@/api/studyProgram";

const { Option } = Select;

const EditSemesterForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
  currentRowData,
}) => {
  const [form] = Form.useForm();
  const [studyProgramList, setStudyProgramList] = useState([]);
  const [loadingPrograms, setLoadingPrograms] = useState(false);

  const fetchStudyProgramList = async () => {
    setLoadingPrograms(true);
    try {
      const result = await getStudyPrograms();
      if (result.data.statusCode === 200) {
        setStudyProgramList(result.data.content);
      } else {
        message.error("Gagal mengambil data program studi");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    } finally {
      setLoadingPrograms(false);
    }
  };

  useEffect(() => {
    if (visible) {
      fetchStudyProgramList();
    }
  }, [visible]);

  useEffect(() => {
    if (visible && currentRowData) {
      form.setFieldsValue({
        idSemester: currentRowData.idSemester,
        namaSemester: currentRowData.namaSemester,
        // JSON mapping dari backend menggunakan camelCase.
        idStudyProgram: currentRowData.studyProgram?.idSchool ?? null,
      });
    }
  }, [visible, currentRowData, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      onOk(values);
    } catch (error) {
      console.error("Validation failed:", error);
    }
  };

  return (
    <Modal
      title="Edit Semester"
      open={visible}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
      okText="Simpan"
      cancelText="Batal"
      width={500}
    >
      <Form form={form} layout="vertical">
        {/* Hidden field untuk ID semester */}
        <Form.Item name="idSemester" hidden>
          <Input />
        </Form.Item>

        <Row gutter={16}>
          <Col xs={24} sm={24} md={24}>
            <Form.Item
              label="Program Studi:"
              name="idStudyProgram"
              rules={[{ required: true, message: "Silahkan pilih Program Studi" }]}
            >
              <Select
                placeholder="Pilih Program Studi"
                loading={loadingPrograms}
                showSearch
                optionFilterProp="children"
              >
                {studyProgramList.map((item) => (
                  <Option key={item.id} value={item.id}>
                    {item.name}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={24}>
            <Form.Item
              label="Nama Semester:"
              name="namaSemester"
              rules={[{ required: true, message: "Silahkan isikan Nama Semester" }]}
            >
              <Input placeholder="Contoh: Semester 1" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};

export default EditSemesterForm;
